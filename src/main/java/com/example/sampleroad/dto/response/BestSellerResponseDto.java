package com.example.sampleroad.dto.response;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.dto.response.cart.ICartProductInfo;
import com.example.sampleroad.dto.response.product.ProductInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class BestSellerResponseDto implements ICartProductInfo {

    private CategoryType productType;
    private int productNo;
    private int stockCnt;
    private String productName;
    private String brandName;
    private String imageUrl;
    private String displayCategoryNo;
    private int salePrice;
    private int immediateDiscountAmt;

    public BestSellerResponseDto(CategoryType productType, int productNo, int stockCnt,
                                 String productName, String brandName, String imageUrl,
                                 String displayCategoryNo,
                                 int salePrice, int immediateDiscountAmt){
        this.productType = productType;
        this.productNo = productNo;
        this.stockCnt = stockCnt;
        this.productName = productName;
        this.brandName = brandName;
        this.imageUrl = imageUrl;
        this.displayCategoryNo = displayCategoryNo;
        this.salePrice = salePrice;
        this.immediateDiscountAmt = immediateDiscountAmt;
    }

    public BestSellerResponseDto(CategoryType productType, int productNo, int stockCnt,
                                 String productName, String brandName, String imageUrl,
                                 int salePrice, int immediateDiscountAmt){
        this.productType = productType;
        this.productNo = productNo;
        this.stockCnt = stockCnt;
        this.productName = productName;
        this.brandName = brandName;
        this.imageUrl = imageUrl;
        this.salePrice = salePrice;
        this.immediateDiscountAmt = immediateDiscountAmt;
    }

    public BestSellerResponseDto(CategoryType productType, ProductInfoDto productInfo){
        this.productType = productType;
        this.productNo = productInfo.getProductNo();
        this.stockCnt = productInfo.getStockCnt();
        this.productName = productInfo.getProductName();
        this.brandName = productInfo.getBrandName();
        this.imageUrl = productInfo.getImageUrl();
        this.displayCategoryNo = productInfo.getDisplayCategoryNo();
        this.salePrice = productInfo.getSalePrice();
        this.immediateDiscountAmt = productInfo.getImmediateDiscountAmt();
    }

    @NoArgsConstructor
    @Getter
    public static class BestSeller {
        private String sectionTitle;
        private String sectionSubTitle;
        private int categoryNo;
        private List<BestSellerResponseDto> products;

        public BestSeller(int categoryNo, List<BestSellerResponseDto> products) {
            this.categoryNo = categoryNo;
            this.products = products;
        }

        public BestSeller(String sectionTitle, String sectionSubTitle,
                          int categoryNo, List<BestSellerResponseDto> products) {
            this.sectionTitle = sectionTitle;
            this.sectionSubTitle = sectionSubTitle;
            this.categoryNo = categoryNo;
            this.products = products;
        }
    }
}
