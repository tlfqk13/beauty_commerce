package com.example.sampleroad.repository.samplekit;

import com.example.sampleroad.dto.response.sampleKit.QSampleKitQueryDto_SampleKit;
import com.example.sampleroad.dto.response.sampleKit.SampleKitQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.product.QProduct.product;
import static com.example.sampleroad.domain.sample.QSampleKit.sampleKit;

public class SampleKitRepositoryImpl implements SampleKitRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public SampleKitRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<SampleKitQueryDto.SampleKit> findSampleKitByProductNoIn(List<Integer> productNos) {
        return queryFactory
                .select(new QSampleKitQueryDto_SampleKit(
                        sampleKit.id,
                        sampleKit.kitName,
                        sampleKit.kitProductNo,
                        product.imgUrl
                ))
                .from(sampleKit)
                .innerJoin(product).on(sampleKit.kitProductNo.eq(product.productNo))
                .where(sampleKit.kitProductNo.in(productNos))
                .fetch();
    }
}
