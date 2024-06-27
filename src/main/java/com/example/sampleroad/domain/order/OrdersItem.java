package com.example.sampleroad.domain.order;

import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORDERS_ITEM")
public class OrdersItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERS_ITEM_ID")
    private Long id;

    @Column(name = "ORDER_OPTION_NO")
    private int orderOptionNo;

    @Column(name = "PRODUCT_CNT")
    private int productCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Orders orders;

    @Builder
    public OrdersItem(Product product, int orderOptionNo, int productCnt, Orders orders) {
        this.product = product;
        this.orderOptionNo = orderOptionNo;
        this.productCnt = productCnt;
        this.orders = orders;
    }

    public void updateOrdersOptionNo(int orderOptionNo) {
        this.orderOptionNo = orderOptionNo;
    }
}
