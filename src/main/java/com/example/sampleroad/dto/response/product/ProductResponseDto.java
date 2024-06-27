package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.dto.response.cart.ICartProductInfo;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ProductResponseDto implements ICartProductInfo {
    private CategoryType categoryType;
    private int productNo;
    private int stockCnt;
    private String productName;
    private String brandName;
    private int brandNo;
    private String imageUrl;
    private String displayCategoryNo;
    private int salePrice;
    private int immediateDiscountAmt;

    public ProductResponseDto(HomeResponseDto.ProductSectionDto productSectionDto) {
        this.categoryType = productSectionDto.getCategoryType();
        this.productNo = productSectionDto.getProductNo();
        this.stockCnt = productSectionDto.getStockCnt();
        this.productName = productSectionDto.getProductName();
        this.brandName = productSectionDto.getBrandName();
        this.imageUrl = productSectionDto.getImageUrl();
        this.displayCategoryNo = productSectionDto.getDisplayCategoryNo();
        this.salePrice = productSectionDto.getSalePrice();
        this.immediateDiscountAmt = productSectionDto.getImmediateDiscountAmt();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ProductResponseDto(CategoryType categoryType, int productNo, int stockCnt,
                              String productName, String brandName, int brandNo,
                              String imageUrl, String displayCategoryNo,
                              int salePrice, int immediateDiscountAmt) {
        this.categoryType = categoryType;
        this.productNo = productNo;
        this.stockCnt = stockCnt;
        this.productName = productName;
        this.brandName = brandName;
        this.brandNo = brandNo;
        this.imageUrl = imageUrl;
        this.displayCategoryNo = displayCategoryNo;
        this.salePrice = salePrice;
        this.immediateDiscountAmt = immediateDiscountAmt;
    }

    public ProductResponseDto(CategoryType categoryType, int productNo, String productName, String brandName,
                              String imageUrl) {
        this.categoryType = categoryType;
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.imageUrl = imageUrl;
    }

    @NoArgsConstructor
    @Getter
    public static class MdKit {
        private String sectionTitle;
        private String sectionSubTitle;
        private List<ProductResponseDto> products;

        public MdKit(String sectionTitle, String sectionSubTitle,
                     List<ProductResponseDto> products) {
            this.sectionTitle = sectionTitle;
            this.sectionSubTitle = sectionSubTitle;
            this.products = products;
        }

        public MdKit(List<ProductResponseDto> products) {
            this.products = products;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class MdPick {
        private String sectionTitle;
        private String sectionSubTitle;
        private Integer categoryNo;

        private List<ProductResponseDto> products;

        public MdPick(String sectionTitle, String sectionSubTitle, Integer categoryNo,
                      List<ProductResponseDto> products) {
            this.sectionTitle = sectionTitle;
            this.sectionSubTitle = sectionSubTitle;
            this.categoryNo = categoryNo;
            this.products = products;
        }
    }
}
