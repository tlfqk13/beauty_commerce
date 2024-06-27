package com.example.sampleroad.controller;

import com.example.sampleroad.dto.response.CategoryResponseDto;
import com.example.sampleroad.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"카테고리 관련 api Controller"})
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    @GetMapping("/api/category")
    @ApiOperation(value = "카테고리 조회")
    public CategoryResponseDto.FlatCategory  getAllCategory(){
        return categoryService.getAllCategory();
    }
}
