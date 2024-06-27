package com.example.sampleroad.domain.grouppurchase;

import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GROUP_PURCHASE_ROOM_PRODUCT")
public class GroupPurchaseRoomProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_PURCHASE_ROOM_PRODUCT_ID")
    private Long id;

    @Column(name = "ROOM_CAPACITY")
    private int roomCapacity;

    @Column(name = "ROOM_PRODUCT_MAX_COUNT")
    private int roomProductMaxCount;

    @Column(name = "ROOM_MAX_COUNT")
    private int roomMaxCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;


}
