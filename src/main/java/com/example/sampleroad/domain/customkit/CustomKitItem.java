package com.example.sampleroad.domain.customkit;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CUSTOMKIT_ITEM")
public class CustomKitItem extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMKIT_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMKIT_ID")
    private CustomKit customKit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int count;

    @Column(name = "PRODUCT_OPTION_NUMBER")
    private int productOptionNumber;

    @Column(name = "ORDER_OPTION_NUMBER")
    private int orderOptionNumber;

    @Builder
    public CustomKitItem(CustomKit customKit, Product product, int count, int productOptionNumber){
        this.customKit = customKit;
        this.product = product;
        this.count = count;
        this.productOptionNumber = productOptionNumber;
    }


    public void updateCustomKitItemCount(int orderCnt) {
        this.count += orderCnt;
    }

    public void updateOrderOption(int orderOptionNo) {
        this.orderOptionNumber = orderOptionNo;
    }
}
