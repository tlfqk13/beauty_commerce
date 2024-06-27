package com.example.sampleroad.repository.experience;

import com.example.sampleroad.domain.experience.ExperienceStatus;
import com.example.sampleroad.dto.response.experience.ExperienceResponseQueryDto;
import com.example.sampleroad.dto.response.experience.QExperienceResponseQueryDto_ExperienceInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.experience.QExperience.experience;
import static com.example.sampleroad.domain.experience.QExperienceMember.experienceMember;
import static com.example.sampleroad.domain.member.QMember.member;

public class ExperienceRepositoryImpl implements ExperienceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ExperienceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ExperienceResponseQueryDto.ExperienceInfo> findExperience(Pageable pageable, Long memberId, List<ExperienceStatus> experienceStatus) {

        BooleanExpression baseCondition = experienceMember.member.id.eq(memberId)
                .and(experienceMember.experience.isVisible.isTrue());

        if (!experienceStatus.contains(ExperienceStatus.ALL)) {
            baseCondition = baseCondition
                    .and(experienceMember.memberExperienceStatus.in(experienceStatus));
        }

        List<ExperienceResponseQueryDto.ExperienceInfo> content = queryFactory
                .select(new QExperienceResponseQueryDto_ExperienceInfo(
                        experience.id,
                        experience.imageUrl,
                        experience.experienceStartTime,
                        experience.experienceFinishTime,
                        experience.winnerNoticeTime,
                        experienceMember.memberExperienceStatus
                )).from(experienceMember)
                .innerJoin(experienceMember.experience, experience)
                .innerJoin(experienceMember.member, member)
                .where(baseCondition)
                .orderBy(experience.experienceStartTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(experienceMember.count())
                .from(experienceMember)
                .innerJoin(experienceMember.experience, experience)
                .innerJoin(experienceMember.member, member)
                .where(baseCondition)
                .orderBy(experience.experienceStartTime.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<ExperienceResponseQueryDto.ExperienceInfo> findExperience(Pageable pageable, ExperienceStatus experienceStatus) {

        BooleanExpression baseCondition = experience.isVisible.isTrue();

        if (!ExperienceStatus.ALL.equals(experienceStatus)) {
            baseCondition = baseCondition.and(experience.experienceStatus.in(experienceStatus));
        }

        List<ExperienceResponseQueryDto.ExperienceInfo> content = queryFactory
                .select(new QExperienceResponseQueryDto_ExperienceInfo(
                        experience.id,
                        experience.imageUrl,
                        experience.experienceStartTime,
                        experience.experienceFinishTime,
                        experience.winnerNoticeTime,
                        experience.experienceStatus
                )).from(experience)
                .where(baseCondition)
                .orderBy(experience.experienceStartTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(experience.count())
                .from(experience)
                .where(baseCondition)
                .orderBy(experience.experienceStartTime.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }
}
