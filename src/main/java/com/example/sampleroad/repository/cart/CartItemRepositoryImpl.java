package com.example.sampleroad.repository.cart;

import com.example.sampleroad.dto.response.cart.CartItemQueryDto;
import com.example.sampleroad.dto.response.cart.QCartItemQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.cart.QCart.cart;
import static com.example.sampleroad.domain.cart.QCartItem.cartItem;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;

public class CartItemRepositoryImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CartItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CartItemQueryDto> findByMemberAndCartIdsIn(Long memberId, List<Long> cartIds) {
        return queryFactory
                .select(new QCartItemQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productNo,
                        cart.cartNo,
                        cartItem.productCount,
                        product.productOptionsNo,
                        product.category.categoryDepth1,
                        cart.isCustomKit
                ))
                .from(cartItem)
                .innerJoin(cartItem.cart, cart)
                .innerJoin(cartItem.product, product)
                .innerJoin(cart.member, member)
                .where(cart.member.id.eq(memberId)
                        .and(cartItem.cart.id.in(cartIds)))
                .fetch();
    }

    @Override
    public List<CartItemQueryDto> findProductByMemberId(Long memberId) {
        return queryFactory
                .select(new QCartItemQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productNo,
                        cart.cartNo,
                        cartItem.productCount,
                        product.productOptionsNo,
                        product.category.categoryDepth1,
                        cart.isCustomKit
                ))
                .from(cartItem)
                .innerJoin(cartItem.cart, cart)
                .innerJoin(cartItem.product, product)
                .innerJoin(product.category, category)
                .innerJoin(cart.member, member)
                .where(cart.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public Page<CartItemQueryDto> findCartProductOver7days(Pageable pageable) {

        LocalDate sevenDaysAgoDate = LocalDate.now().minusDays(8);
        LocalDateTime startOfSevenDaysAgo = sevenDaysAgoDate.atStartOfDay(); // Start of the day 7 days ago
        LocalDateTime endOfSevenDaysAgo = LocalDate.now().atTime(LocalTime.MAX); // End of the day 7 days ago

        List<CartItemQueryDto> content = queryFactory
                .select(new QCartItemQueryDto(
                        cart.id,
                        cart.member.id,
                        product.productNo,
                        cart.cartNo,
                        cartItem.productCount,
                        product.productOptionsNo,
                        product.category.categoryDepth1,
                        cart.isCustomKit
                ))
                .from(cartItem)
                .innerJoin(cartItem.cart, cart)
                .innerJoin(cartItem.product, product)
                .innerJoin(product.category, category)
                .innerJoin(cart.member, member)
                .where(cartItem.modifiedAt.between(startOfSevenDaysAgo,endOfSevenDaysAgo).and(cart.member.withdrawal.isFalse()))
                .groupBy(cart.member.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(cart.member.id.countDistinct())
                .from(cartItem)
                .innerJoin(cartItem.cart, cart)
                .innerJoin(cartItem.product, product)
                .innerJoin(product.category, category)
                .innerJoin(cart.member, member)
                .where(cartItem.modifiedAt.between(startOfSevenDaysAgo,endOfSevenDaysAgo).and(cart.member.withdrawal.isFalse()))
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<CartItemQueryDto> findByMemberId(Long memberId) {
        return queryFactory
                .select(new QCartItemQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productNo,
                        cart.cartNo,
                        cartItem.productCount,
                        product.productOptionsNo,
                        product.category.categoryDepth1,
                        cart.isCustomKit
                ))
                .from(cartItem)
                .innerJoin(cartItem.cart, cart)
                .innerJoin(cartItem.product, product)
                .innerJoin(product.category, category)
                .innerJoin(cart.member, member)
                .where(cart.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        List<Long> cartIds = queryFactory
                .select(cart.id)
                .from(cart)
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(cart.member.id.eq(memberId))
                .fetch();

        if (!cartIds.isEmpty()) {
            queryFactory
                    .delete(cartItem)
                    .where(cartItem.cart.id.in(cartIds))
                    .execute();
        }
    }
}
