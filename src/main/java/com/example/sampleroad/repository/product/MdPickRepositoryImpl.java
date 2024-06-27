package com.example.sampleroad.repository.product;

import com.example.sampleroad.dto.response.product.MdPickQueryDto;
import com.example.sampleroad.dto.response.product.QMdPickQueryDto_MdPickItemInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.product.QMdPick.mdPick;
import static com.example.sampleroad.domain.product.QProduct.product;

public class MdPickRepositoryImpl implements MdPickRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public MdPickRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<MdPickQueryDto.MdPickItemInfo> findMdKit(Pageable pageable) {
        List<MdPickQueryDto.MdPickItemInfo> content = queryFactory
                .select(new QMdPickQueryDto_MdPickItemInfo(
                        product.productNo,
                        product.productName,
                        product.brandName,
                        product.tag,
                        product.imgUrl,
                        product.category.categoryDepth1
                ))
                .from(mdPick)
                .innerJoin(mdPick.product,product)
                .innerJoin(product.category,category)
                .where(product.productInvisible.isFalse())
                .orderBy(product.productNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(mdPick.count())
                .from(mdPick)
                .where(product.productInvisible.isFalse())
                .orderBy(product.productNo.desc())
                .innerJoin(mdPick.product,product)
                .innerJoin(product.category,category)
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }
}
