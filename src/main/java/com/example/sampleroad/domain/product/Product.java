package com.example.sampleroad.domain.product;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PRODUCT")
public class Product extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_NO")
    private int productNo; // 샵바이 연결을 위한 상품 No.

    @Column(name = "BRAND_NAME")
    private String brandName;

    @Column(name = "BRAND_NO")
    private int brandNo;

    @Column(name = "PRODUCT_TAGS")
    private String tag;

    @Column(name = "PRODUCT_IMGURL")
    @Lob
    private String imgUrl;

    @Column(name = "PRODUCT_OPTIONS_NO")
    private int productOptionsNo;

    @Column(name = "PRODUCT_INVISIBLE")
    private boolean productInvisible;

    @Column(name = "PRODUCT_VIEW_COUNT")
    private Long productViewCount;

    @Column(name = "PRODUCT_DISCOUNT_RATE")
    private double productDiscountRate;

    @Column(name = "PRODUCT_REVIEW_RATE")
    private double productReviewRate;

    @Column(name = "IS_MULTI_PURCHASE")
    private Boolean isMultiPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Builder
    public Product(int productNo, String brandName, int brandNo, String productName, String imgUrl, Category category) {
        this.productNo = productNo;
        this.brandName = brandName;
        this.brandNo = brandNo;
        this.productName = productName;
        this.imgUrl = imgUrl;
        this.category = category;
        this.productViewCount = 1L;
        this.productReviewRate = 0.0;
    }

    public void updateProductOptionNo(int optionNo) {
        this.productOptionsNo = optionNo;
    }

    public void updateProductViewCount(Long productViewCount) {
        this.productViewCount = productViewCount + 1;
    }

    public void updateProductDiscountRate(double productDiscountRate) {
        this.productDiscountRate = productDiscountRate;
    }

    public void updateProductImgUrl(String imageUrl) {
        this.imgUrl = imageUrl;
    }

    public void updateProductReviewRate(Double reviewRateAvg) {
        this.productReviewRate = reviewRateAvg;
    }

    public void updateProductBrandNo(int brandNo) {
        this.brandNo = brandNo;
    }
}
