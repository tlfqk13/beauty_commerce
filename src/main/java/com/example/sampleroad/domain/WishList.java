package com.example.sampleroad.domain;

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
@Table(name = "WISHLIST")
public class WishList extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WISHLIST_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Builder
    public WishList(Member member, Product product) {
        this.member = member;
        this.product = product;
    }
}
