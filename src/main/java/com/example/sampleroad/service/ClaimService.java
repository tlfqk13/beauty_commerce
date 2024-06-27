package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.customkit.CustomKit;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.dto.request.ClaimRequestDto;
import com.example.sampleroad.dto.response.customkit.CustomKitItemQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.customkit.CustomKitRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ClaimService {

    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.accept-header}")
    String acceptHeader;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;

    @Value("${shop-by.login-url}")
    String loginUrl;

    @Value("${shop-by.send-authentication-number-url}")
    String sendAuthenticationNumberUrl;

    @Value("${shop-by.check-member-url}")
    String profile;

    Gson gson = new Gson();

    private final MemberRepository memberRepository;
    private final CustomKitRepository customKitRepository;

    public void updateClaimWithdraw(UserDetailsImpl userDetails, ClaimRequestDto.UpdateClaimWithdraw claimWithdraw, String orderNo) {

        // orderOptionNo를 db에서 관리 안하기 때문에 앱에서 받아야함.
        List<CustomKitItemQueryDto> customKitItems = customKitRepository.findByOrderNoIn(orderNo);

        for (ClaimRequestDto.ClaimDto updateClaimWithdraw : claimWithdraw.getUpdateClaim()) {
            boolean shouldWithdraw = false;

            if (!customKitItems.isEmpty()) {
                for (CustomKitItemQueryDto customKitItem : customKitItems) {
                    if (updateClaimWithdraw.getOrderOptionNo() == customKitItem.getProductOptionNo()) {
                        shouldWithdraw = true;
                        break;
                    }
                }
            } else {
                shouldWithdraw = true;
            }

            if (shouldWithdraw) {
                claimWithDrawShopby(updateClaimWithdraw.getClaimNo(), userDetails.getMember().getShopByAccessToken());
            }
        }
    }

    private void claimWithDrawShopby(String claimNo, String shopByAccessToken) {
        try {
            HttpResponse<String> response = Unirest.put(
                            shopByUrl + profile + "/claims" + "/" + claimNo + "/withdraw")
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("accesstoken", shopByAccessToken)
                    .header("content-type", acceptHeader)
                    .asString();

            log.info("클레임 철회하기_____________________________");
            log.info(response.getBody());
            log.info("클레임 철회하기_____________________________");

            if (response.getStatus() != 204) {
                ShopBy.errorMessage(response);
            }

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void returnClaim(ClaimRequestDto.ReturnClaims dto, UserDetailsImpl userDetails, String orderNo) {

        if (!dto.getClaimedProductOptions().isEmpty()) {
            returnClaimShopby(userDetails.getMember().getShopByAccessToken(), dto);
        } else {
            // 커스텀키트 환불은 - claimedProductOptions 비어서 보내고
            // orderNo를 param으로 받는다 -> 주문번호로 커스텀키트 조회
            List<CustomKitItemQueryDto> customKitItem = customKitRepository.findByOrderNoIn(orderNo);
            if (!customKitItem.isEmpty()) {
                for (CustomKitItemQueryDto customKitItemQueryDto : customKitItem) {
                    List<ClaimRequestDto.ClaimedProductOption> claimedProductOptions = new ArrayList<>();
                    ClaimRequestDto.ClaimedProductOption claimedProductOption =
                            new ClaimRequestDto.ClaimedProductOption(
                                    // 어차피 주문 상세 조회에서 해당 주문건 orderOptionNo를 업데이트한다.
                                    customKitItemQueryDto.getProductOptionNo(),
                                    customKitItemQueryDto.getOrderCnt());

                    claimedProductOptions.add(claimedProductOption);

                    ClaimRequestDto.ReturnClaims returnClaims =
                            new ClaimRequestDto.ReturnClaims(dto, claimedProductOptions);

                    returnClaimShopby(userDetails.getMember().getShopByAccessToken(), returnClaims);
                }
            }
        }
    }

    private void returnClaimShopby(String shopByAccessToken, ClaimRequestDto.ReturnClaims dto) {
        try {
            HttpResponse<String> response = Unirest.post(
                            shopByUrl + profile + "/claims" + "/return")
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("accesstoken", shopByAccessToken)
                    .header("content-type", acceptHeader)
                    .body(gson.toJson(dto))
                    .asString();

            log.info("반품 신청하기_____________________________");
            log.info(response.getBody());
            log.info("반품 신청하기_____________________________");

            if (response.getStatus() != 204) {
                ShopBy.errorMessage(response);
            }

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void exchangeClaim(ClaimRequestDto.ExchangeClaims dto, UserDetailsImpl userDetails, String orderOptionNo, String orderNo) {

        CustomKit customKit = customKitRepository.findByMemberIdAndIsOrderedAndOrderNo(userDetails.getMember().getId(), true, orderNo)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.CUSTOMKIT_NOT_FOUND));

        for (int i = 0; i < customKit.getCustomKitItems().size(); i++) {
            ClaimRequestDto.ExchangeOption exchangeOption =
                    new ClaimRequestDto.ExchangeOption(dto.getExchangeOption().getInputTexts()
                            , customKit.getCustomKitItems().get(i).getCount()
                            , customKit.getCustomKitItems().get(i).getProductOptionNumber()
                            , customKit.getCustomKitItems().get(i).getProduct().getProductNo());

            ClaimRequestDto.ExchangeClaims exchangeClaims =
                    new ClaimRequestDto.ExchangeClaims(dto, exchangeOption);

            exchangeClaimShopby(userDetails.getMember().getShopByAccessToken(), exchangeClaims
                    , String.valueOf(customKit.getCustomKitItems().get(i).getOrderOptionNumber()));

        }

        // 관리자키트 교환 신청?
        exchangeClaimShopby(userDetails.getMember().getShopByAccessToken(), dto, orderOptionNo);
    }

    private void exchangeClaimShopby(String shopByAccessToken, ClaimRequestDto.ExchangeClaims dto, String
            orderOptionNo) {
        try {
            HttpResponse<String> response = Unirest.post(
                            shopByUrl + profile + "/order-options/" + orderOptionNo + "/claims/exchange")
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("accesstoken", shopByAccessToken)
                    .header("content-type", acceptHeader)
                    .body(gson.toJson(dto))
                    .asString();

            log.info("교환 신청하기_____________________________");
            log.info(response.getBody());
            log.info("교환 신청하기_____________________________");

            if (response.getStatus() != 204) {
                ShopBy.errorMessage(response);
            }

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

}
