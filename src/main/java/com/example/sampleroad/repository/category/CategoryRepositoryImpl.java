package com.example.sampleroad.repository.category;

import com.example.sampleroad.domain.Category;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.QCategory.category;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CategoryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Category> findCategoriesByDepthNumbers(List<Integer> categoryNos, boolean isGetDepth2) {
        BooleanExpression condition = isGetDepth2
                ? category.categoryDepthNumber3.eq(0).and(category.categoryDepthNumber2.in(categoryNos))
                : category.categoryDepthNumber3.in(categoryNos).or(category.categoryDepthNumber2.in(categoryNos));

        return queryFactory
                .selectFrom(category)
                .where(condition)
                .fetch();
    }
}
