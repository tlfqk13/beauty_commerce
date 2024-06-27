package com.example.sampleroad.repository.home;

import com.example.sampleroad.domain.product.HomeProductType;
import com.example.sampleroad.dto.response.home.HomeProductResponseQueryDto;
import com.example.sampleroad.dto.response.home.QHomeProductResponseQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.home.QHome.home;
import static com.example.sampleroad.domain.product.QProduct.product;

public class HomeRepositoryImpl implements HomeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public HomeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<HomeProductResponseQueryDto> findHomeProductByHomeType(HomeProductType homeProductType) {

        return queryFactory
                .select(new QHomeProductResponseQueryDto(
                        home.homeProductType,
                        product.id,
                        product.productNo,
                        product.productName,
                        product.brandName,
                        product.tag,
                        product.imgUrl,
                        product.category.categoryDepth1,
                        home.localDateTime
                ))
                .from(home)
                .innerJoin(home.product,product)
                .innerJoin(product.category,category)
                .where(home.homeProductType.in(homeProductType))
                .fetch();
    }
}
