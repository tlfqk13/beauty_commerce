package com.example.sampleroad.domain.customkit;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CUSTOMKIT")
public class CustomKit extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMKIT_ID")
    private Long id;

    @Column(name = "CATEGORY_TYPE_1")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryDepth1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column(name = "ORDER_SHEET_NO")
    private String orderSheetNo;

    @Column(name = "ORDER_NO")
    private String orderNo;

    @Column(name = "IS_ORDERED")
    private Boolean isOrdered;

    @OneToMany(mappedBy = "customKit", cascade = CascadeType.ALL)
    private List<CustomKitItem> customKitItems = new ArrayList<>();

    @Builder
    public CustomKit(Member member, List<CustomKitItem> customKitItems) {
        this.member = member;
        this.customKitItems = customKitItems;
        this.categoryDepth1 = CategoryType.CUSTOMKIT;
        this.isOrdered = false;
    }

    public void addCustomKitItem(CustomKitItem customKitItem){
        this.customKitItems.add(customKitItem);
    }

    public void updateOrderSheetNo(String orderSheetNo) {
        this.orderSheetNo = orderSheetNo;
    }

    public void updateOrderNo(String orderNo){
        this.orderNo = orderNo;
    }
    public void updateIsOrdered(boolean isOrdered){
        this.isOrdered = isOrdered;
    }

    public void updateClearOrderNo() {
        this.orderNo = null;
    }
}
