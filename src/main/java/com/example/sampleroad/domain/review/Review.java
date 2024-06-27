package com.example.sampleroad.domain.review;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REVIEW")
@AllArgsConstructor
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;

    @Column(name = "TAG")
    @Lob
    private String tag;

    @Column(name = "CONTENT")
    @Lob
    private String content;

    @Column(name = "REVIEW_NO")
    private int reviewNo;

    @Column(name = "IS_REPORT")
    private Boolean isReport;

    @Column(name = "REVIEW_RATE")
    private double reviewRate;

    @Column(name = "RECOMMEND_COUNT")
    private int recommendCount;

    @Column(name = "IS_SAMPLEROAD_REVIEW")
    private boolean isSampleRoadReview;

    @Column(name = "ORDER_OPTION_NO")
    private Long orderOptionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public Review(String tag, String content, int reviewNo, Long orderOptionNo, double reviewRate, Product product, Member member) {
        this.tag = tag;
        this.content = content;
        this.reviewNo = reviewNo;
        this.orderOptionNo = orderOptionNo;
        this.reviewRate = reviewRate;
        this.isReport = false;
        this.recommendCount = 0;
        this.product = product;
        this.member = member;
    }

    public boolean getIsSampleRoadReview() {
        return isSampleRoadReview;
    }

    public void updateReview(String content, String reviewTags) {
        this.content = content;
        this.tag = reviewTags;
    }

    public void updateReviewReport(Boolean isReport) {
        this.isReport = isReport;
    }

    public void updateReviewRecommendCount(Boolean isRecommend, int recommendCount) {
        if (isRecommend) {
            if (recommendCount >= 0) {
                this.recommendCount += 1;
            }
        } else {
            if (recommendCount > 0) {
                this.recommendCount -= 1;
            } else {
                this.recommendCount = 0;
            }
        }
    }

    public Boolean getReport() {
        return isReport;
    }

    public void updateReviewRate(double reviewRate) {
        this.reviewRate = reviewRate;
    }

    public void updateOrderOptionNo(Long orderOptionNo) {
        this.orderOptionNo = orderOptionNo;
    }
}


