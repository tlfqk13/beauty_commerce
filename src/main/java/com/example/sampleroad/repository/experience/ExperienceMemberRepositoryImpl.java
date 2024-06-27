package com.example.sampleroad.repository.experience;

import com.example.sampleroad.dto.response.experience.ExperienceResponseQueryDto;
import com.example.sampleroad.dto.response.experience.QExperienceResponseQueryDto_ExperienceMemberInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.experience.QExperience.experience;
import static com.example.sampleroad.domain.experience.QExperienceMember.experienceMember;
import static com.example.sampleroad.domain.member.QMember.member;

public class ExperienceMemberRepositoryImpl implements ExperienceMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ExperienceMemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ExperienceResponseQueryDto.ExperienceMemberInfo> findExperienceMemberByMemberId(Long memberId, List<Long> experienceIds) {
        return queryFactory
                .select(new QExperienceResponseQueryDto_ExperienceMemberInfo(
                        experienceMember.id,
                        experienceMember.memberExperienceStatus,
                        experienceMember.member.id,
                        experienceMember.experience.id
                ))
                .from(experienceMember)
                .innerJoin(experienceMember.member,member)
                .innerJoin(experienceMember.experience,experience)
                .where(experienceMember.member.id.eq(memberId)
                        .and(experienceMember.experience.id.in(experienceIds)))
                .fetch();
    }

    @Override
    public long findByExperienceId(Long experienceId) {
        return queryFactory
                .selectFrom(experienceMember)
                .where(experienceMember.experience.id.eq(experienceId))
                .fetch().size();
    }

    @Override
    public List<ExperienceResponseQueryDto.ExperienceMemberInfo> findByIsWinner(Long experienceId, boolean isWinner) {
        return queryFactory
                .select(new QExperienceResponseQueryDto_ExperienceMemberInfo(
                        experienceMember.id,
                        experienceMember.memberExperienceStatus,
                        experienceMember.member.id,
                        experienceMember.experience.id
                ))
                .from(experienceMember)
                .innerJoin(experienceMember.member,member)
                .innerJoin(experienceMember.experience,experience)
                .where(experienceMember.experience.id.in(experienceId)
                        .and(experienceMember.isWinner.in(isWinner)))
                .fetch();
    }

    @Override
    public List<ExperienceResponseQueryDto.ExperienceMemberInfo> findExperienceMemberByExperienceId(Long experienceId) {
        return queryFactory
                .select(new QExperienceResponseQueryDto_ExperienceMemberInfo(
                        experienceMember.id,
                        experienceMember.memberExperienceStatus,
                        experienceMember.member.id,
                        experienceMember.experience.id
                ))
                .from(experienceMember)
                .innerJoin(experienceMember.member,member)
                .innerJoin(experienceMember.experience,experience)
                .where(experienceMember.experience.id.in(experienceId))
                .fetch();
    }
}
