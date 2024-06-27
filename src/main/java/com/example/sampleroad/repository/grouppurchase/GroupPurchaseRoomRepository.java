package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupPurchaseRoomRepository extends JpaRepository<GroupPurchaseRoom, Long>, GroupPurchaseRoomRepositoryCustom {
    @Modifying(clearAutomatically = true)
    @Query("update GroupPurchaseRoom gpr set gpr.isFull = true where gpr.id = :roomId")
    void updateRoomIsFull(Long roomId);

    List<GroupPurchaseRoom> findByIdIn(List<Long> roomIds);
}
