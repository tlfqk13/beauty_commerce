package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.response.home.PopUpResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.PopUpService;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PopUpController {
    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.check-member-url}")
    String profile;

    @Value("${shop-by.orders-url}")
    String ordersUrl;

    @Value("${shop-by.accept-header}")
    String acceptHeader;
    @Value("${shop-by.version-header}")
    String versionHeader;
    Gson gson = new Gson();
    @Value("${shop-by.platform-header}")
    String platformHeader;

    private final PopUpService popUpService;

    /**
     * 팝업 상세 -> 팝업 클릭시 이동
     * @param popupId
     * @return popupDetail
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/09/12
     **/
    @GetMapping("/api/popup/{popupId}")
    @ResponseBody
    public PopUpResponseDto.PopUpDetail getPopUpDetail(@AuthenticationPrincipal UserDetailsImpl userDetail,
                                                       @PathVariable Long popupId) throws UnirestException, ParseException {
        return popUpService.getPopUpDetail(userDetail, popupId);

    }

    // TODO: 2023/09/14 파업 오늘 하루 보지 않기 -> 상태 업데이트
    @PutMapping("/api/popup")
    @ResponseBody
    public ResultInfo updatePopUpUserVisibleToday(@AuthenticationPrincipal UserDetailsImpl userDetail){
        popUpService.updatePopUpUserVisibleToday(userDetail);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "팝업 상태 변경 완료");
    }
}
