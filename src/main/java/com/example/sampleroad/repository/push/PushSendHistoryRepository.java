package com.example.sampleroad.repository.push;

import com.example.sampleroad.domain.push.PushSendHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface PushSendHistoryRepository extends JpaRepository<PushSendHistory,Long>, PushSendHistoryRepositoryCustom{

    @Modifying(clearAutomatically = true)
    @Query("update PushSendHistory psh set psh.isRead = true " +
            "where psh.id in (:unreadPushHistoryIds)")
    void updateUnreadStatusAll(@Param("unreadPushHistoryIds") Set<Long> unreadPushHistoryIds);
    PushSendHistory findFirstByMemberIdAndPushId(Long memberId,Long pushId);
}
