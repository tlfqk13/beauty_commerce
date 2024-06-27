package com.example.sampleroad.dto.response;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PaymentResponseDto {

    private String orderSheetNo;
    private String result;
    private String message;

    public PaymentResponseDto(String orderSheetNo, String result, String message) {
        this.orderSheetNo = orderSheetNo;
        this.result = result;
        this.message = message;
    }

    @NoArgsConstructor
    @Getter
    public static class Confirm {
        private PaymentResponseDto.GroupPurchaseInfo groupPurchaseInfo;
        private RecommendProductList recommendProduct;

        public Confirm(GroupPurchaseInfo groupPurchaseInfo, RecommendProductList recommendProductList) {
            this.groupPurchaseInfo = groupPurchaseInfo;
            this.recommendProduct = recommendProductList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseInfo {
        private GroupPurchaseType groupType;
        private int remainMemberCnt;
        private String endDate;
        private List<String> groupMemberImgUrls;

        public GroupPurchaseInfo(GroupPurchaseType groupType, int remainMemberCnt, String endDate, List<String> groupMemberImgUrls) {
            this.groupType = groupType;
            this.remainMemberCnt = remainMemberCnt;
            this.endDate = endDate;
            this.groupMemberImgUrls = groupMemberImgUrls;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RecommendProductList {
        private String title;
        private String subTitle;
        private List<?> productList;

        public RecommendProductList(String title, String subTitle, List<?> productList) {
            this.title = title;
            this.subTitle = subTitle;
            this.productList = productList;
        }
    }
}
