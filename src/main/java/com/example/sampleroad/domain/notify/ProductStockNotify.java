package com.example.sampleroad.domain.notify;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PRODUCT_STOCK_NOTIFY")
public class ProductStockNotify extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_STOCK_NOTIFY_ID")
    private Long id;

    @Column(name = "PRODUCT_STOCK_NOTIFICATION_AGREE")
    private Boolean productStockNotificationAgree;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ProductStockNotify(Member member, Boolean productStockNotificationAgree) {
        this.productStockNotificationAgree = productStockNotificationAgree;
        this.member = member;
    }

    public void updateProductStockNotificationAgree(boolean isNotify) {
        this.productStockNotificationAgree = isNotify;
    }
}