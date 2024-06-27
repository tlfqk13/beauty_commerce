package com.example.sampleroad.domain.grouppurchase;

import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GROUP_PURCHASE_ROOM")
public class GroupPurchaseRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_PURCHASE_ROOM_ID")
    private Long id;

    @Column(name = "DEADLINE")
    private LocalDateTime deadLine;

    @Column(name = "IS_FULL")
    private boolean isFull;

    @Column(name = "ROOM_CAPACITY")
    private int roomCapacity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    public boolean getIsFull() {
        return isFull;
    }

    @Builder
    public GroupPurchaseRoom(int roomCapacity, LocalDateTime deadLine, Product product) {
        this.roomCapacity = roomCapacity;
        this.product = product;
        this.deadLine = deadLine;
        this.isFull = false;
    }
}
