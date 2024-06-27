package com.example.sampleroad.repository.push;

import com.example.sampleroad.domain.push.PushMessage;
import com.example.sampleroad.domain.push.PushMessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushMessageRepository extends JpaRepository<PushMessage,Long> {

    Optional<PushMessage> findByPushMessageType(PushMessageType pushMessageType);

}