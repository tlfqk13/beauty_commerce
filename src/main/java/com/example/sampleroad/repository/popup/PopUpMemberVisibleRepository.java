package com.example.sampleroad.repository.popup;

import com.example.sampleroad.domain.popup.PopUpMemberVisible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PopUpMemberVisibleRepository extends JpaRepository<PopUpMemberVisible, Long> {

    Optional<PopUpMemberVisible> findByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from popup_member_visible as pmv" +
            " where pmv.created_at < :createdDate" +
            " limit 2000", nativeQuery = true)
    void deleteAllByInQuery(@Param("createdDate") LocalDateTime createdDate);
}
