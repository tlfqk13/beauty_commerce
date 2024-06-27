package com.example.sampleroad.dto.response.display;

import com.example.sampleroad.domain.DisplaySectionType;
import com.example.sampleroad.dto.response.coupon.CouponResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class DisplayResponseDto {
    private int eventNo;
    private String mainTitle;
    private String shareDescription;
    private List<SectionInfo> sections;

    public DisplayResponseDto(int eventNo, String label, String shareDescription, List<SectionInfo> sections) {
        this.eventNo = eventNo;
        this.mainTitle = label;
        this.shareDescription = shareDescription;
        this.sections = sections;
    }

    @NoArgsConstructor
    @Getter
    public static class ImageSection {
        private DisplaySectionType displaySectionType;
        private List<ImageUrl> imageUrl;

        public ImageSection(DisplaySectionType displaySectionType, List<ImageUrl> imageUrl) {
            this.displaySectionType = displaySectionType;
            this.imageUrl = imageUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ImageUrl {
        private String url;

        public ImageUrl(String url) {
            this.url = "https:" + url;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class SectionInfo {
        private DisplaySectionType displaySectionType;
        private String sectionTitle;
        private String imageUrl;
        private DisplayProductInfo displayProductInfo;
        private DisplayCoupon couponInfo;

        public SectionInfo(DisplaySectionType displaySectionType, DisplayCoupon couponInfo) {
            this.displaySectionType = displaySectionType;
            this.couponInfo = couponInfo;
        }

        public SectionInfo(DisplaySectionType displaySectionType, DisplayProductInfo displayProductInfo) {
            this.displaySectionType = displaySectionType;
            this.displayProductInfo = displayProductInfo;
        }

        public SectionInfo(DisplaySectionType displaySectionType, String imageUrl, String sectionTitle) {
            this.displaySectionType = displaySectionType;
            this.imageUrl = imageUrl;
            this.sectionTitle = sectionTitle;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DisplayList {
        int totalCount;
        int totalPage;
        List<DisplayInfo> displayInfoList;

        public DisplayList(int totalCount, int totalPage, List<DisplayInfo> displayInfoList) {
            this.totalCount = totalCount;
            this.totalPage = totalPage;
            this.displayInfoList = displayInfoList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DisplayInfo {
        private int eventNo;
        private String label;
        private String eventId;
        private String mainImageUrl;
        private String imageUrl;
        private String endYmdt;
        private String progressStatus;
        private Boolean hasCoupon;

        public boolean getHasCoupon() {
            return hasCoupon;
        }

        public DisplayInfo(int eventNo, String label,
                           String eventId, String endYmdt) {
            this.eventNo = eventNo;
            this.label = label;
            this.eventId = eventId;
            this.endYmdt = endYmdt;
        }

        public DisplayInfo(int eventNo, String label, String eventId,
                           String mainImageUrl,
                           String imageUrl, String progressStatus,
                           Boolean hasCoupon) {
            this.eventNo = eventNo;
            this.label = label;
            this.eventId = eventId;
            this.mainImageUrl = mainImageUrl;
            this.imageUrl = imageUrl;
            this.progressStatus = progressStatus;
            this.hasCoupon = hasCoupon;
        }

        public DisplayInfo(int eventNo, String label, String eventId,
                           String imageUrl, String progressStatus,
                           Boolean hasCoupon) {
            this.eventNo = eventNo;
            this.label = label;
            this.eventId = eventId;
            this.imageUrl = imageUrl;
            this.progressStatus = progressStatus;
            this.hasCoupon = hasCoupon;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DisplayCoupon extends CouponResponseDto.BaseCouponInfo {
        private String maxDiscountAmtStr;
        private boolean isDownloadable;

        public DisplayCoupon(int couponNo, String couponName, String couponType,
                             int discountRate, int discountAmt, String minSalePriceStr,
                             boolean isDownloadable, String maxDiscountAmtStr, String couponTitle) {
            this.maxDiscountAmtStr = maxDiscountAmtStr;
            this.isDownloadable = isDownloadable;
            this.couponNo = couponNo;
            this.couponName = couponName;
            this.couponType = couponType;
            this.discountRate = discountRate;
            this.discountAmt = discountAmt;
            this.minSalePriceStr = minSalePriceStr;
            this.couponTitle = couponTitle;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DisplayProductInfoList {
        private String sectionLabel;
        private List<DisplayProductInfo> displayProductInfos;

        public DisplayProductInfoList(String sectionLabel, List<DisplayProductInfo> displayProductInfos) {
            this.sectionLabel = sectionLabel;
            this.displayProductInfos = displayProductInfos;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DisplayProductInfo {
        private String sectionLabel;
        private int productNo;
        private String productName;
        private String brandName;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;
        private String imageUrl;

        public DisplayProductInfo(String sectionLabel, int productNo,
                                  String productName,String brandName,
                                  int salePrice, int immediateDiscountAmt,
                                  int stockCnt, String imageUrl) {
            this.sectionLabel = sectionLabel;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnt;
            this.imageUrl = imageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class DisplayDetailInfo {
        private DisplayInfo displayInfo;
        private List<DisplayProductInfoList> displayProductInfoList;
        private List<DisplayCoupon> displayCouponList;
        private String shareDescription;

        public DisplayDetailInfo(DisplayInfo displayInfo,
                                 List<DisplayProductInfoList> displayProductInfoList,
                                 List<DisplayCoupon> displayCouponList,
                                 String shareDescription) {
            this.displayInfo = displayInfo;
            this.displayProductInfoList = displayProductInfoList;
            this.displayCouponList = displayCouponList;
            this.shareDescription = shareDescription;
        }
    }
}
