package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.AdminProductRequestDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.AdminCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"상품등록 관련 관리자 api Controller"})
public class ProductAdminController {

    private final AdminCartService adminCartService;

    @PostMapping("/api/admin/product-add")
    public ResultInfo addProductByAdmin(@RequestBody AdminProductRequestDto dto) {
        HashMap<String, Object> addProductByAdmin = adminCartService.addProductByAdmin(dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "관리자가 상품 등록 완료", addProductByAdmin);
    }

    @PostMapping("/api/admin/product-update")
    @ApiOperation(value = "상품 optionNo 추가")
    public ResultInfo updateProductOptionNo(@RequestBody AdminProductRequestDto dto) {

        HashMap<String, Object> resultInfo = adminCartService.updateProductOptionNo(dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "상품 옵션 번호 업데이트 완료", resultInfo);
    }

    @GetMapping("/api/admin/product-update")
    @ApiOperation(value = "상품 update")
    public ResultInfo updateProductInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        adminCartService.updateProductInfo(userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "상품 업데이트 완료");
    }
}
