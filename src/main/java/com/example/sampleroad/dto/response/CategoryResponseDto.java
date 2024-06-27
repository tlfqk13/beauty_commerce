package com.example.sampleroad.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CategoryResponseDto {
    private String depth2Icon;
    private String depth3Icon;
    private int depth1CategoryNo;
    private String depth1Label;
    private int depth1DisplayOrder;
    private String fullCategoryName;
    private int depth2CategoryNo;
    private String depth2Label;
    private int depth2DisplayOrder;
    private int depth3CategoryNo;
    private String depth3Label;
    private int depth3DisplayOrder;
    private int depth4CategoryNo;
    private String depth4Label;
    private int depth4DisplayOrder;
    private int depth5CategoryNo;
    private String depth5Label;
    private int depth5DisplayOrder;

    public CategoryResponseDto(String depth2Icon, String depth3Icon, int depth1CategoryNo,
                               String depth1Label, int depth1DisplayOrder,
                               int depth2CategoryNo, String depth2Label, int depth2DisplayOrder,
                               int depth3CategoryNo, String depth3Label, int depth3DisplayOrder,
                               int depth4CategoryNo, String depth4Label, int depth4DisplayOrder,
                               int depth5CategoryNo, String depth5Label, int depth5DisplayOrder,
                               String fullCategoryName) {

        this.depth2Icon = depth2Icon;
        this.depth3Icon = depth3Icon;

        this.depth1CategoryNo = depth1CategoryNo;
        this.depth2CategoryNo = depth2CategoryNo;
        this.depth3CategoryNo = depth3CategoryNo;
        this.depth4CategoryNo = depth4CategoryNo;
        this.depth5CategoryNo = depth5CategoryNo;

        this.depth1Label = depth1Label;
        this.depth2Label = depth2Label;
        this.depth3Label = depth3Label;
        this.depth4Label = depth4Label;
        this.depth5Label = depth5Label;

        this.depth1DisplayOrder = depth1DisplayOrder;
        this.depth2DisplayOrder = depth2DisplayOrder;
        this.depth3DisplayOrder = depth3DisplayOrder;
        this.depth4DisplayOrder = depth4DisplayOrder;
        this.depth5DisplayOrder = depth5DisplayOrder;

        this.fullCategoryName = fullCategoryName;

    }

    @NoArgsConstructor
    @Getter
    public static class FlatCategory {
        private List<CategoryResponseDto> flatCategories;

        public FlatCategory(List<CategoryResponseDto> dto) {
            this.flatCategories = dto;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class HomeCategoryInfo{
        private String depth2Icon;
        private String depth3Icon;
        private String categoryNameDepth2;
        private String categoryNameDepth3;
        private int categoryNoDepth2;
        private int categoryNoDepth3;

        public HomeCategoryInfo(String depth2Icon, String depth3Icon,
                                String categoryNameDepth2, String categoryNameDepth3,
                                int categoryNoDepth2, int categoryNoDepth3) {
            this.depth2Icon = depth2Icon;
            this.depth3Icon = depth3Icon;
            this.categoryNameDepth2 = categoryNameDepth2;
            this.categoryNameDepth3 = categoryNameDepth3;
            this.categoryNoDepth2 = categoryNoDepth2;
            this.categoryNoDepth3 = categoryNoDepth3;
        }
    }


}
