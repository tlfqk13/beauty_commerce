package com.example.sampleroad.repository.product;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.dto.response.order.QOrderResponseDto_SampleList;
import com.example.sampleroad.dto.response.product.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.product.QProduct.product;
import static com.example.sampleroad.domain.review.QReview.review;
import static com.example.sampleroad.domain.sample.QSampleKit.sampleKit;
import static com.example.sampleroad.domain.sample.QSampleKitItem.sampleKitItem;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<OrderResponseDto.SampleList> findSampleKitListByOrder(Set<Integer> productNo) {

        return queryFactory
                .select(new QOrderResponseDto_SampleList(
                        sampleKit.kitName,
                        sampleKit.kitProductNo,
                        product.productNo,
                        product.brandName,
                        product.productName,
                        product.imgUrl
                ))
                .from(product)
                .innerJoin(sampleKitItem).on(product.id.eq(sampleKitItem.product.id))
                .innerJoin(sampleKit).on(sampleKitItem.sampleKit.id.eq(sampleKit.id))
                .where(sampleKit.kitProductNo.in(productNo))
                .fetch();
    }

    @Override
    public List<ProductDetailResponseDto.SampleList> findSampleListByProductNoIn(List<Integer> productNoList) {
        return queryFactory
                .select(new QProductDetailResponseDto_SampleList(
                        product.productNo,
                        product.productName,
                        product.brandName,
                        product.imgUrl,
                        sampleKit.kitProductNo
                ))
                .from(product)
                .innerJoin(sampleKitItem).on(product.id.eq(sampleKitItem.product.id))
                .innerJoin(sampleKit).on(sampleKitItem.sampleKit.id.eq(sampleKit.id))
                .where(sampleKit.kitProductNo.in(productNoList))
                .fetch();
    }

    @Override
    public List<ProductDetailResponseDto.SampleList> findSampleList(int kitCategoryNo) {
        return queryFactory
                .select(new QProductDetailResponseDto_SampleList(
                        product.productNo,
                        product.productName,
                        product.brandName,
                        product.imgUrl,
                        product.category.categoryDepthNumber1
                ))
                .from(product)
                .innerJoin(product.category, category)
                .where(
                        product.category.categoryDepthNumber1.eq(kitCategoryNo) // Add this condition
                )
                .fetch();
    }

    @Override
    public boolean existsByProductNo(int productNo) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(product)
                .where(product.productNo.eq(productNo))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public List<CustomKitQueryDto> findCustomKitItemByProductNos(Set<Integer> productNos) {

        return queryFactory
                .select(new QCustomKitQueryDto(
                        product.productNo,
                        product.productOptionsNo,
                        product.productName,
                        product.brandName,
                        product.imgUrl,
                        product.productReviewRate
                ))
                .from(product)
                .where(product.productNo.in(productNos)
                        .and(product.productInvisible.isFalse()))
                .orderBy(product.productNo.desc())
                .fetch();
    }

    @Override
    public Page<ProductQueryDto.SearchProductQueryDto> findBySearchKeywordPaging(Pageable pageable, String searchKeyword) {
        List<ProductQueryDto.SearchProductQueryDto> content = queryFactory
                .select(new QProductQueryDto_SearchProductQueryDto(
                        product.productNo
                ))
                .from(product)
                .innerJoin(product.category, category)
                .where(product.productInvisible.isFalse()
                        // TODO: 2023/09/25 할인율 검색에서는 우선 이벤트 상품 제외
                        .and(product.category.categoryDepth2.notIn(CategoryType.EVENT))
                        .and(product.productName.contains(searchKeyword)))
                .orderBy(product.productDiscountRate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(product.count())
                .from(product)
                .innerJoin(product.category, category)
                .where(product.productInvisible.isFalse()
                        // TODO: 2023/09/25 할인율 검색에서는 우선 이벤트 상품 제외
                        .and(product.category.categoryDepth2.notIn(CategoryType.EVENT))
                        .and(product.productName.contains(searchKeyword)))
                .orderBy(product.productDiscountRate.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<ProductQueryDto.SearchProductQueryDto> findBySearchKeywordPaging(Pageable pageable, int brandNo) {
        List<ProductQueryDto.SearchProductQueryDto> content = queryFactory
                .select(new QProductQueryDto_SearchProductQueryDto(
                        product.productNo
                ))
                .from(product)
                .innerJoin(product.category, category)
                .where(product.productInvisible.isFalse()
                        // TODO: 2023/09/25 할인율 검색에서는 우선 이벤트 상품 제외
                        .and(product.category.categoryDepth2.notIn(CategoryType.EVENT))
                        .and(product.brandNo.eq(brandNo)))
                .orderBy(product.productDiscountRate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(product.count())
                .from(product)
                .innerJoin(product.category, category)
                .where(product.productInvisible.isFalse()
                        // TODO: 2023/09/25 할인율 검색에서는 우선 이벤트 상품 제외
                        .and(product.category.categoryDepth2.notIn(CategoryType.EVENT))
                        .and(product.brandNo.eq(brandNo)))
                .orderBy(product.productDiscountRate.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<ProductReviewInfoDto> findProductReviewInfo(List<Integer> kitItemsProductNos) {
        return queryFactory
                .select(new QProductReviewInfoDto(
                        review.product.productNo,
                        review.product.productOptionsNo,
                        review.reviewRate.avg(),
                        review.count()
                ))
                .from(review)
                .innerJoin(review.product,product)
                .where(product.productNo.in(kitItemsProductNos))
                .fetch();
    }

    @Override
    public List<ProductQueryDto> findProductCategoryByProductNos(List<Integer> productNos) {
        return queryFactory
                .select(new QProductQueryDto(
                        product.productNo,
                        product.productOptionsNo,
                        product.productName,
                        product.imgUrl,
                        product.productReviewRate,
                        product.category.categoryDepth1,
                        product.isMultiPurchase
                        ))
                .from(product)
                .innerJoin(product.category, category)
                .where(product.productNo.in(productNos))
                .fetch();

    }

    @Override
    public List<ProductQueryDto> findProductCategoryType2ByProductNos(List<Integer> productNos) {
        return queryFactory
                .select(new QProductQueryDto(
                        product.productNo,
                        product.productOptionsNo,
                        product.productName,
                        product.imgUrl,
                        product.productReviewRate,
                        product.category.categoryDepth2,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(product.category, category)
                .where(product.productNo.in(productNos))
                .fetch();

    }

    @Override
    public List<ProductQueryDto> findProductCategoryByProductNos(String skinType) {
        return queryFactory
                .select(new QProductQueryDto(
                        product.productNo,
                        product.productOptionsNo,
                        product.productName,
                        product.imgUrl,
                        product.productReviewRate,
                        product.category.categoryDepth1,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(product.category, category)
                .where(product.tag.in(skinType))
                .limit(30)
                .fetch();
    }
}
