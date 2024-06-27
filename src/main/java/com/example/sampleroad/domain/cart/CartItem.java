package com.example.sampleroad.domain.cart;

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
@Table(name = "CART_ITEM")
public class CartItem extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ITEM_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "PRODUCT_COUNT")
    private int productCount;

    @Column(name = "PRODUCT_OPTION_NUMBER")
    private int productOptionNumber;

    @Builder
    public CartItem(Cart cart, Product product, int productCount, int productOptionNumber) {
        this.cart = cart;
        this.product = product;
        this.productCount = productCount;
        this.productOptionNumber = productOptionNumber;
    }

    public void updateCartItemCount(int count) {
        this.productCount = count;
    }
}
