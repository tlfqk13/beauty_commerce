package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.popup.PopUp;
import com.example.sampleroad.domain.popup.PopUpDataType;
import com.example.sampleroad.domain.popup.PopUpMemberVisible;
import com.example.sampleroad.dto.response.PopUpQueryDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.dto.response.home.PopUpResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.popup.PopUpDetailRepository;
import com.example.sampleroad.repository.popup.PopUpMemberVisibleRepository;
import com.example.sampleroad.repository.popup.PopUpRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopUpService {
    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.check-member-url}")
    String profile;

    @Value("${shop-by.accept-header}")
    String acceptHeader;
    @Value("${shop-by.version-header}")
    String versionHeader;
    Gson gson = new Gson();
    @Value("${shop-by.platform-header}")
    String platformHeader;

    private final PopUpRepository popUpRepository;
    private final PopUpDetailRepository popUpDetailRepository;
    private final PopUpMemberVisibleRepository popUpMemberVisibleRepository;
    private final MemberRepository memberRepository;

    /**
     * 팝업 상세 -> 팝업 클릭시 이동
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/12
     **/
    public PopUpResponseDto.PopUpDetail getPopUpDetail(UserDetailsImpl userDetail, Long popupId) throws UnirestException, ParseException {

        PopUpQueryDto popupDetail = popUpDetailRepository.findPopUpDetail(popupId);
        if (popupDetail == null) {
            throw new ErrorCustomException(ErrorCode.POPUP_NOT_FOUND);
        }
        String popUpCondition = popupDetail.getPopUpCondition();

        if (popUpCondition != null) {
            if (popUpCondition.equals("FIRST_PURCHASE")) {
                boolean isFirstPurchase = shopbyGetOrderList(userDetail.getMember().getShopByAccessToken(), userDetail.getMember().getMemberLoginId());
                // 현재 쿠폰팝업은 구매내역있는 고객에게만 보여주기 때문에 이딴식?
                if (isFirstPurchase) {
                    return new PopUpResponseDto.PopUpDetail();
                }
            }
        }

        // TODO: 2023/12/12 팝업 detail 변경으로 인한 -> 코드 리팩토링 구역
        if (popupDetail.getIsMovePopupDetail()) {
            if (PopUpDataType.POPUP_DETAIL.equals(popupDetail.getPopUpDataType())) {
                return new PopUpResponseDto.PopUpDetail(popupDetail.getPopUpId(), popupDetail.getPopUpDetailImageUrl());
            }
        }
        return new PopUpResponseDto.PopUpDetail();
    }

    /**
     * PopUpService의 설명을 여기에 작성한다.
     * member 쿼리문을 단 1번만 하기 위해서 여기서 조회한 member데이터를 넘긴다
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/12
     **/
    public HashMap<String, Boolean> getPopUpMemberVisible(UserDetailsImpl userDetails) {
        HashMap<String, Boolean> resultMap = new HashMap<>();

        popUpMemberVisibleRepository.findByMemberId(userDetails.getMember().getId())
                .ifPresent(popUpMemberVisible -> {
                    resultMap.put("isVisibleToday", popUpMemberVisible.isVisibleToday());
                });

        if (resultMap.isEmpty()) {
            // Handle the case when there's no result, for example, set default values.
            resultMap.put("isVisibleToday", true);
        }

        return resultMap;
    }

    @Transactional
    public void updatePopUpUserVisibleToday(UserDetailsImpl userDetail) {

        Member member = getMember(userDetail);

        popUpMemberVisibleRepository.findByMemberId(userDetail.getMember().getId())
                .ifPresentOrElse(
                        PopUpMemberVisible::updateIsVisibleToday,
                        () -> popUpMemberVisibleRepository.save(PopUpMemberVisible.builder().member(member).build())
                );
    }

    private boolean shopbyGetOrderList(String shopByAccessToken, String memberLoginId) throws UnirestException, ParseException {
        boolean isFirstPurchase;

        HttpResponse<String> response = Unirest.get(shopByUrl + profile + "/orders")
                .queryString("orderRequestTypes", "PAY_DONE,PRODUCT_PREPARE," +
                        "DELIVERY_PREPARE,DELIVERY_ING," +
                        "DELIVERY_DONE," +
                        "BUY_CONFIRM")
                .queryString("pageNumber", 1)
                .queryString("pageSize", 50)
                .queryString("hasTotalCount", true)
                .queryString("startYmd", CustomValue.defaultStartYmd)
                .queryString("endYmd", "")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .asString();

        log.info("배송/주문 조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("response-> " + response.getBody());
        log.info("배송/주문 조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        int totalCount = jsonObject.get("totalCount").getAsInt();

        isFirstPurchase = (totalCount == 0) || CustomValue.adminAccount.equals(memberLoginId);

        return isFirstPurchase;
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    /**
     * 매일 자정에 '오늘 하루 안보기 테이블 삭제'
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/18
     **/
    @Transactional
    public void deletePopupMemberVisible() {
        LocalDateTime endOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        System.out.println("endOfDay " + endOfDay);
        popUpMemberVisibleRepository.deleteAllByInQuery(endOfDay);
    }

    /**
     * 홈에서 팝업 리스트 조횐
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/7/24
     **/
    public HomeResponseDto.PopUpList getPopUpList(UserDetailsImpl userDetails) {
        HashMap<String, Boolean> popUpMemberVisible = getPopUpMemberVisible(userDetails);
        List<PopUpResponseDto.HomePopUpInfo> popUpSection = new ArrayList<>();
        if (popUpMemberVisible.get("isVisibleToday")) {
            List<PopUp> popUps = popUpRepository.findByIsVisible(true);
            for (PopUp popUp : popUps) {
                PopUpResponseDto.HomePopUpInfo popUpInfo = new PopUpResponseDto.HomePopUpInfo(
                        popUp.getId(),
                        popUp.getMainImageUrl(),
                        popUp.getPopupKeyNo(),
                        popUp.getPopUpDataType(),
                        popUp.getIsMovePopupDetail(),
                        popUp.getExternalUrl()
                );
                popUpSection.add(popUpInfo);
            }
        }
        return new HomeResponseDto.PopUpList(popUpMemberVisible.getOrDefault("isVisibleToday", true), popUpSection);
    }
}
