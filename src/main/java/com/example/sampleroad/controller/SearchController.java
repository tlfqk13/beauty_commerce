package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.response.BrandResponseDto;
import com.example.sampleroad.dto.response.search.SearchResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.SearchService;
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
@Api(tags = {"검색 관련 api Controller"})
@Slf4j
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/api/search/main")
    @ApiOperation("제품 검색 메인")
    public SearchResponseDto.SearchMain getSearchMain(@AuthenticationPrincipal UserDetailsImpl userDetail) throws UnirestException, ParseException {
        if (userDetail == null) {
            log.info("비회원 검색 메인");
            return searchService.getSearchMainForNotMember();
        }
        return searchService.getSearchMain(userDetail);
    }

    @GetMapping("/api/new-search/main")
    @ApiOperation("제품 검색 메인")
    public SearchResponseDto.SearchMain getNewSearchMain(@AuthenticationPrincipal UserDetailsImpl userDetail) throws UnirestException, ParseException {
        return searchService.getNewSearchMain(userDetail);
    }

    @GetMapping("/api/search/product")
    @ApiOperation(value = "제품 검색하기 api")
    public SearchResponseDto getSearchProduct(@AuthenticationPrincipal UserDetailsImpl userDetail,
                                              @RequestParam(defaultValue = "") String searchKeyword,
                                              @RequestParam(defaultValue = "POPULAR") SearchSortType searchSortType,
                                              @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
                                              @RequestParam(defaultValue = CustomValue.defaultSearchPageSize) int pageSize) throws UnirestException, ParseException {
        if (userDetail == null) {
            return searchService.getSearchProductForNotMember(pageNumber, pageSize, searchKeyword, searchSortType);
        }
        return searchService.getSearchProduct(pageNumber, pageSize, userDetail, searchKeyword, searchSortType);
    }


    // TODO: 4/18/24 브랜드 api 상품영역 위에 이미지 영역 나눠야합니다 
    @GetMapping("/api/search/brand")
    @ApiOperation(value = "브랜드 모아보기 api")
    public BrandResponseDto getSearchBrandProduct(@AuthenticationPrincipal UserDetailsImpl userDetail,
                                                  @RequestParam(defaultValue = "") int brandNo,
                                                  @RequestParam(defaultValue = "POPULAR") SearchSortType searchSortType,
                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = "100") int pageSize,
                                                  @RequestParam(defaultValue = "0") int categoryNumber) throws UnirestException, ParseException {
        return searchService.getSearchBrandProduct(userDetail, brandNo, searchSortType, pageNumber, pageSize, categoryNumber);
    }

    @GetMapping("/api/search/brand/product-info")
    @ApiOperation(value = "브랜드 모아보기 api")
    public BrandResponseDto.BrandProductInfoDto getSearchBrandProductInfo(@AuthenticationPrincipal UserDetailsImpl userDetail,
                                                                          @RequestParam(defaultValue = "") int brandNo,
                                                                          @RequestParam(defaultValue = "POPULAR") SearchSortType searchSortType,
                                                                          @RequestParam(defaultValue = "1") int pageNumber,
                                                                          @RequestParam(defaultValue = "100") int pageSize,
                                                                          @RequestParam(defaultValue = "0") int categoryNumber) throws UnirestException, ParseException {
        return searchService.getSearchBrandProductInfo(userDetail, brandNo, searchSortType, pageNumber, pageSize, categoryNumber);
    }
}
