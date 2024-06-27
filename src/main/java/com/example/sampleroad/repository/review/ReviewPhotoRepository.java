package com.example.sampleroad.repository.review;

import com.example.sampleroad.domain.review.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto,Long>, ReviewPhotoRepositoryCustom {
    @Modifying(clearAutomatically = true)
    @Query("delete from ReviewPhoto rp where rp.id in :reviewPhotoIds")
    void deleteAllByReviewIdInQuery(@Param("reviewPhotoIds") List<Long> reviewPhotoIds);
}
