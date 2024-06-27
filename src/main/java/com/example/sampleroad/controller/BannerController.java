package com.example.sampleroad.controller;

import com.example.sampleroad.dto.response.banner.BannerDetailResponseDto;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import com.example.sampleroad.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"배너 관련 api Controller"})
public class BannerController {

    private final BannerService bannerService;
    @GetMapping("/api/banner-list")
    @ApiOperation(value = "배너 전체 리스트 조회 API")
    public BannerResponseDto getBannerList(){
        return bannerService.getBannerList();
    }

    @GetMapping("/api/banner/{bannerId}")
    @ApiOperation(value = "배너 상세 API")
    public BannerDetailResponseDto.BannerDetailResponse getBannerDetail(@PathVariable Long bannerId){
        return bannerService.getBannerDetail(bannerId);
    }
}
