package com.example.sampleroad.repository.cart;

import com.example.sampleroad.dto.response.cart.CartQueryDto;
import com.example.sampleroad.dto.response.cart.QCartQueryDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.sampleroad.domain.cart.QCart.cart;
import static com.example.sampleroad.domain.cart.QCartItem.cartItem;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;

public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CartRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<CartQueryDto> findCartByMemberIdAndProductNoAndOptionNo(Long memberId, int productNo, int optionNo) {
        return Optional.ofNullable(queryFactory
                .select(new QCartQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productName,
                        product.productNo,
                        cartItem.productCount,
                        cartItem.productOptionNumber,
                        cart.isCustomKit,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(cartItem).on(product.id.eq(cartItem.product.id))
                .innerJoin(cart).on(cartItem.cart.id.eq(cart.id))
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(cart.member.id.eq(memberId)
                        .and(product.productNo.eq(productNo))
                        .and(cartItem.productOptionNumber.eq(optionNo)))
                .fetchOne());
    }

    @Override
    public List<CartQueryDto> findCartByProductNo(int productNo) {
        return queryFactory
                .select(new QCartQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productName,
                        product.productNo,
                        cartItem.productCount,
                        cartItem.productOptionNumber,
                        cart.isCustomKit,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(cartItem).on(product.id.eq(cartItem.product.id))
                .innerJoin(cart).on(cartItem.cart.id.eq(cart.id))
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(product.productNo.eq(productNo))
                .fetch();
    }

    @Override
    public List<CartQueryDto> findCartByCartIds(Long memberId, List<Long> cartIds) {
        return queryFactory
                .select(new QCartQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productName,
                        product.productNo,
                        cartItem.productCount,
                        cartItem.productOptionNumber,
                        cart.isCustomKit,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(cartItem).on(product.id.eq(cartItem.product.id))
                .innerJoin(cart).on(cartItem.cart.id.eq(cart.id))
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(cart.member.id.eq(memberId)
                        .and(cart.id.in(cartIds)))
                .fetch();
    }

    @Override
    public List<CartQueryDto> findCartInfoByMemberId(Long memberId) {
        return queryFactory
                .select(new QCartQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productName,
                        product.productNo,
                        cartItem.productCount,
                        cartItem.productOptionNumber,
                        cart.isCustomKit,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(cartItem).on(product.id.eq(cartItem.product.id))
                .innerJoin(cart).on(cartItem.cart.id.eq(cart.id))
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(cart.member.id.eq(memberId)
                        .and(product.productInvisible.isFalse()))
                .fetch();
    }

    @Override
    public List<CartQueryDto> findCartInfoByMemberIdAndProductNos(Long memberId, List<Integer> productNos) {
        return queryFactory
                .select(new QCartQueryDto(
                        cart.id,
                        cartItem.id,
                        product.productName,
                        product.productNo,
                        cartItem.productCount,
                        cartItem.productOptionNumber,
                        cart.isCustomKit,
                        product.isMultiPurchase
                ))
                .from(product)
                .innerJoin(cartItem).on(product.id.eq(cartItem.product.id))
                .innerJoin(cart).on(cartItem.cart.id.eq(cart.id))
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(product.productNo.in(productNos)
                        .and(member.id.eq(memberId)))
                .fetch();
    }

    @Override
    public List<Tuple> countByMemberId(Long memberId) {
        NumberExpression<Long> countFalse = Expressions.numberTemplate(Long.class, "coalesce(sum(case when {0} = false then 1 else 0 end), 0)", cart.isCustomKit);
        NumberExpression<Long> countTrue = Expressions.numberTemplate(Long.class, "coalesce(sum(case when {0} = true then 1 else 0 end), 0)", cart.isCustomKit);

        return queryFactory
                .select(countFalse.as("countFalse")
                        , countTrue.as("countTrue"))
                .from(cartItem)
                .innerJoin(cartItem.cart, cart)
                .innerJoin(cart.member, member)
                .where(member.id.eq(memberId))
                .fetch();
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(cart)
                .innerJoin(member).on(cart.member.id.eq(member.id))
                .where(cart.member.id.eq(memberId))
                .fetchFirst();

        return fetchFirst != null;
    }

}
