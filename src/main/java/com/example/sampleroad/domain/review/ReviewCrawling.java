package com.example.sampleroad.domain.review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REVIEW_CRAWLING")
@AllArgsConstructor
public class ReviewCrawling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;

    @Column(name = "PRODUCT_NO")
    private int productNo;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "REVIEW_LINK")
    private String reviewLink;
}
