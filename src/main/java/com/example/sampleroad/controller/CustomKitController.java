package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.response.product.CustomKitResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.CustomKitService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"샘플 키트 만들기 관련 api Controller"})
public class CustomKitController {

    private final CustomKitService customKitService;

    /**
     * 샘플키트 만들기 카테고리 별로 조회하기 - categoryNo로 보내는걸로 변경
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/19
     **/
    @GetMapping("/api/custom-kit")
    @ApiOperation(value = "샘플키트 만들기 카테고리 별로 조회하기 api ")
    public CustomKitResponseDto.CustomKitFromCategory getCustomKitItemByCategory(
            @AuthenticationPrincipal UserDetailsImpl userDetail,
            @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
            @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
            @RequestParam(defaultValue = "0") int categoryNumber,
            @RequestParam(defaultValue = "DEFAULT") SearchSortType condition) throws UnirestException, ParseException {
        return customKitService.getCustomKitItemByCategory(userDetail, pageNumber, pageSize, categoryNumber, condition);
    }
}
