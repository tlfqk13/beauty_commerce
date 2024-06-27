package com.example.sampleroad.repository.push;

import com.example.sampleroad.dto.response.push.PushSendResponseQueryDto;
import com.example.sampleroad.dto.response.push.QPushSendResponseQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.push.QPush.push;
import static com.example.sampleroad.domain.push.QPushSendHistory.pushSendHistory;

public class PushSendHistoryRepositoryImpl implements PushSendHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PushSendHistoryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<PushSendResponseQueryDto> findByMemberId(Long memberId) {
        return queryFactory
                .select(new QPushSendResponseQueryDto(
                        pushSendHistory.id,
                        pushSendHistory.push.id,
                        pushSendHistory.isRead,
                        pushSendHistory.push.pushType,
                        pushSendHistory.push.pushDataType,
                        pushSendHistory.memberId
                ))
                .from(pushSendHistory)
                .innerJoin(pushSendHistory.push,push)
                .innerJoin(member).on(pushSendHistory.memberId.eq(member.id))
                .where(pushSendHistory.memberId.eq(memberId))
                .fetch();
    }
}
