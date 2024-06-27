package com.example.sampleroad.repository.search;

import com.example.sampleroad.dto.response.search.QSearchKeywordQueryDto;
import com.example.sampleroad.dto.response.search.SearchKeywordQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.search.QSearchKeyword.searchKeyword;

public class SearchKeywordRepositoryImpl implements SearchKeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SearchKeywordRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<SearchKeywordQueryDto> findSearchKeywordByIsVisbile() {
        return queryFactory
                .select(new QSearchKeywordQueryDto(
                        searchKeyword.searchWord
                ))
                .from(searchKeyword)
                .where(searchKeyword.isVisible.isTrue())
                .orderBy(searchKeyword.searchCount.desc())
                .distinct()
                .limit(10)
                .fetch();

    }
}
