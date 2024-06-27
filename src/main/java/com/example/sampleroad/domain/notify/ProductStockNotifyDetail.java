package com.example.sampleroad.domain.notify;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PRODUCT_STOCK_NOTIFY_DETAIL")
public class ProductStockNotifyDetail extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_STOCK_NOTIFY_DETAIL_ID")
    private Long id;

    @Column(name = "PRODUCT_STOCK_DETAIL_NOTIFICATION_AGREE")
    private Boolean productStockDetailNotificationAgree;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_STOCK_NOTIFY_ID")
    ProductStockNotify productStockNotify;


    @Builder
    public ProductStockNotifyDetail(Boolean productStockDetailNotificationAgree, Member member, Product product, ProductStockNotify productStockNotify) {
        this.productStockDetailNotificationAgree = productStockDetailNotificationAgree;
        this.productStockNotify = productStockNotify;
        this.member = member;
        this.product = product;
    }

    public void updateNotifyStatus(boolean isNotify) {
        this.productStockDetailNotificationAgree = isNotify;
    }

    public void updateProductStockNotificationAgree(boolean isNotify) {
        this.productStockDetailNotificationAgree = isNotify;
    }
}
