package com.example.sampleroad.repository.wishlist;

import com.example.sampleroad.dto.response.wishList.QWishListQueryDto;
import com.example.sampleroad.dto.response.wishList.WishListQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.QWishList.wishList;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;

public class WishListRepositoryImpl implements WishListRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public WishListRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<WishListQueryDto> findProductIdsByMemberId(Long memberId, Pageable pageable) {
        List<WishListQueryDto> content = queryFactory
                .select(new QWishListQueryDto(
                        wishList.id,
                        product.id,
                        product.productNo,
                        product.category.categoryDepth1
                ))
                .from(wishList)
                .innerJoin(wishList.product, product)
                .innerJoin(wishList.member, member)
                .innerJoin(product.category,category)
                .where(member.id.eq(memberId).and(product.productInvisible.isFalse()))
                .orderBy(wishList.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(wishList.count())
                .from(wishList)
                .innerJoin(wishList.product, product)
                .innerJoin(wishList.member, member)
                .where(member.id.eq(memberId).and(product.productInvisible.isFalse()))
                .orderBy(wishList.createdAt.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public boolean existsByProductNoAndMemberId(int productNo, Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(wishList)
                .innerJoin(wishList.product, product)
                .innerJoin(wishList.member, member)
                .where(wishList.member.id.eq(memberId)
                        .and(wishList.product.productNo.eq(productNo)))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public List<WishListQueryDto> findByProductNosAndMemberId(List<Integer> productNos, Long memberId) {
        return queryFactory
                .select(new QWishListQueryDto(
                        wishList.id,
                        product.id,
                        product.productNo,
                        product.category.categoryDepth1
                ))
                .from(wishList)
                .innerJoin(wishList.product, product)
                .innerJoin(wishList.member, member)
                .innerJoin(product.category,category)
                .where(member.id.eq(memberId).and(product.productInvisible.isFalse())
                        .and(product.productNo.in(productNos)))
                .fetch();
    }

    @Override
    public List<WishListQueryDto> findByProductNosAndMemberId(Set<Integer> productNos, Long memberId) {
        return queryFactory
                .select(new QWishListQueryDto(
                        wishList.id,
                        product.id,
                        product.productNo,
                        product.category.categoryDepth1
                ))
                .from(wishList)
                .innerJoin(wishList.product, product)
                .innerJoin(wishList.member, member)
                .innerJoin(product.category,category)
                .where(member.id.eq(memberId).and(product.productInvisible.isFalse())
                        .and(product.productNo.in(productNos)))
                .fetch();
    }
}
