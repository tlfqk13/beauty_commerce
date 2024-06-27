package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class MdKitQueryDto {


    @NoArgsConstructor
    @Getter
    public static class MdKitItemInfo {
        private int productNo;
        private String productName;
        private String brandName;
        private String tag;
        private String imgUrl;
        private CategoryType categoryType;

        @QueryProjection
        public MdKitItemInfo(int productNo, String productName, String brandName, String tag,
                             String imgUrl, CategoryType categoryType) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.tag = tag;
            this.imgUrl = imgUrl;
            this.categoryType = categoryType;
        }
    }
}
