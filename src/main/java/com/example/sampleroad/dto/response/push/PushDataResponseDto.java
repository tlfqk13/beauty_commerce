package com.example.sampleroad.dto.response.push;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.push.PushDataType;
import com.example.sampleroad.domain.search.SearchSortType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PushDataResponseDto {
    private PushDataType pushDataType;
    private ProductInfo productInfo;
    private CategoryInfo categoryInfo;
    private ExperienceInfo experienceInfo;
    private DisplayInfo displayInfo;
    private OrderInfo orderInfo;

    public PushDataResponseDto(ExperienceInfo experienceInfo,PushDataType pushDataType) {
        this.experienceInfo = experienceInfo;
        this.pushDataType = pushDataType;
    }

    public PushDataResponseDto(DisplayInfo displayInfo,PushDataType pushDataType) {
        this.displayInfo = displayInfo;
        this.pushDataType = pushDataType;
    }

    public PushDataResponseDto(ProductInfo productInfo,PushDataType pushDataType) {
        this.productInfo = productInfo;
        this.pushDataType = pushDataType;
    }

    public PushDataResponseDto(CategoryInfo categoryInfo,PushDataType pushDataType) {
        this.categoryInfo = categoryInfo;
        this.pushDataType = pushDataType;
    }

    public PushDataResponseDto(OrderInfo orderInfo,PushDataType pushDataType) {
        this.orderInfo = orderInfo;
        this.pushDataType = pushDataType;
    }

    public PushDataResponseDto(PushDataType pushDataType) {
        this.pushDataType = pushDataType;
    }


    @NoArgsConstructor
    @Getter
    public static class ProductInfo {
        private Integer productNo;
        private CategoryType productType;

        public ProductInfo(Integer productNo, CategoryType productType) {
            this.productNo = productNo;
            this.productType = productType;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrderInfo {
        private String orderNo;

        public OrderInfo(String orderNo) {
            this.orderNo = orderNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CategoryInfo {
        private int categoryNo;
        private SearchSortType sortType;

        public CategoryInfo(int categoryNo, SearchSortType sortType) {
            this.categoryNo = categoryNo;
            this.sortType = sortType;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExperienceInfo {
        private Long experienceId;

        public ExperienceInfo(Long experienceId) {
            this.experienceId = experienceId;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DisplayInfo {
        private int displayEventNo;

        public DisplayInfo(int displayEventNo) {
            this.displayEventNo = displayEventNo;
        }
    }
}
