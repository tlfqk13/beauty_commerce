package com.example.sampleroad.dto.response;

import com.example.sampleroad.domain.DisplaySectionType;
import com.example.sampleroad.dto.response.coupon.CouponResponseDto;
import com.example.sampleroad.dto.response.search.SearchResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
public class BrandResponseDto {
    private Long totalCount;
    private String mainTitle;
    private String brandDescription;
    private List<BrandResponseDto.SectionInfo> sections;
    private Set<String> categoryNos;

    public BrandResponseDto(Long totalCount, String searchKeyword, String brandDescription,
                            List<SectionInfo> sectionInfoList, Set<String> categoryNos) {
        this.totalCount = totalCount;
        this.mainTitle = searchKeyword;
        this.brandDescription = brandDescription;
        this.sections = sectionInfoList;
        this.categoryNos = categoryNos;
    }

    public BrandResponseDto(Long totalCount) {
        this.totalCount = totalCount;
    }

    @NoArgsConstructor
    @Getter
    public static class ImageSection {
        private List<BrandResponseDto.ImageUrl> imageUrl;

        public ImageSection(List<BrandResponseDto.ImageUrl> imageUrl) {
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
        private List<BrandProductInfo> brandProductInfo;
        private List<BrandResponseDto.BrandCoupon> couponInfo;

        public SectionInfo(DisplaySectionType displaySectionType, String imageUrl) {
            this.displaySectionType = displaySectionType;
            this.imageUrl = imageUrl;
        }

        public SectionInfo(String sectionTitle, DisplaySectionType displaySectionType, List<BrandCoupon> brandCouponInfoList) {
            this.sectionTitle = sectionTitle;
            this.displaySectionType = displaySectionType;
            this.couponInfo = brandCouponInfoList;
        }

        public SectionInfo(DisplaySectionType displaySectionType, String sectionTitle, List<BrandProductInfo> brandProductInfoList, List<BrandCoupon> brandCouponInfoList) {
            this.displaySectionType = displaySectionType;
            this.sectionTitle = sectionTitle;
            this.brandProductInfo = brandProductInfoList;
            this.couponInfo = brandCouponInfoList;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class BrandCoupon extends CouponResponseDto.BaseCouponInfo {
        private String maxDiscountAmtStr;
        private boolean isDownloadable;

        public BrandCoupon(int couponNo, String couponName, String couponType,
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
    public static class BrandProductInfoList {
        private String sectionLabel;
        private List<BrandProductInfo> brandProductInfos;

        public BrandProductInfoList(String sectionLabel, List<BrandProductInfo> brandProductInfos) {
            this.sectionLabel = sectionLabel;
            this.brandProductInfos = brandProductInfos;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BrandProductInfo {
        private int productNo;
        private String productName;
        private String brandName;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;
        private String imageUrl;
        private boolean isWishList;

        public boolean getIsWishList() {
            return isWishList;
        }


        public BrandProductInfo(SearchResponseDto.SearchResultItemInfo dto, boolean isWishList) {
            this.productNo = dto.getProductNo();
            this.productName = dto.getProductName();
            this.brandName = dto.getBrandName();
            this.salePrice = dto.getSalePrice();
            this.immediateDiscountAmt = dto.getImmediateDiscountAmt();
            this.stockCnt = dto.getStockCnt();
            this.imageUrl = dto.getImgUrl();
            this.isWishList = isWishList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BrandDetailInfo {
        private List<BrandResponseDto.BrandProductInfoList> displayProductInfoList;
        private List<BrandResponseDto.BrandCoupon> displayCouponList;
        private String shareDescription;

        public BrandDetailInfo(List<BrandResponseDto.BrandProductInfoList> displayProductInfoList,
                               List<BrandResponseDto.BrandCoupon> displayCouponList,
                               String shareDescription) {
            this.displayProductInfoList = displayProductInfoList;
            this.displayCouponList = displayCouponList;
            this.shareDescription = shareDescription;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BrandProductInfoDto {
        private Long totalCount;
        private List<BrandProductInfo> brandProductInfoList;

        public BrandProductInfoDto(Long totalCount, List<BrandProductInfo> brandProductInfoList) {
            this.totalCount = totalCount;
            this.brandProductInfoList = brandProductInfoList;
        }
    }

}
