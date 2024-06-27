package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.response.wishList.WishListResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.WishListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = {"상품 위시리스트 관련 api Controller"})
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @PostMapping("/api/product/{productNo}/wish-list")
    @ApiOperation(value = "제품 찜하기 api")
    public ResultInfo addProductWishList(@PathVariable int productNo,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        wishListService.addProductWishList(productNo, userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "찜하기 등록 완료");
    }

    @DeleteMapping("/api/product/{productNo}/wish-list")
    @ApiOperation(value = "제품 찜목록 삭제 api")
    public ResultInfo deleteProductWishList(@PathVariable int productNo,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        wishListService.deleteProductWishList(productNo, userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "찜하기 삭제 완료");
    }

    @GetMapping("/api/product/wish-list")
    @ApiOperation(value = "찜한 제품 조회 api")
    public WishListResponseDto.AllWishListFromShopby getAllWishList(
            @AuthenticationPrincipal UserDetailsImpl userDetail,
            @RequestParam(defaultValue = "true") boolean isSorted,
            @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
            @RequestParam(defaultValue = CustomValue.pageSize) int pageSize) {
        return wishListService.getAllWishList(userDetail, isSorted, pageSize, pageNumber);
    }

}
