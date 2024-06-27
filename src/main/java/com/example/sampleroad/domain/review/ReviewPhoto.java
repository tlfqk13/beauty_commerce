package com.example.sampleroad.domain.review;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REVIEW_PHOTO")
@AllArgsConstructor
@Builder
public class ReviewPhoto extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_PHOTO_ID")
    private Long id;

    @Column(name = "REVIEW_PHOTO_URL")
    private String reviewPhotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID")
    private Review review;

    @Builder
    public ReviewPhoto(String reviewPhotoUrl, Review review){
        this.reviewPhotoUrl = reviewPhotoUrl;
        this.review = review;
    }

}
