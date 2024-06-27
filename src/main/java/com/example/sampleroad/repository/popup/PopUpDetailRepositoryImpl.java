package com.example.sampleroad.repository.popup;

import com.example.sampleroad.dto.response.PopUpQueryDto;
import com.example.sampleroad.dto.response.QPopUpQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.example.sampleroad.domain.popup.QPopUp.popUp;
import static com.example.sampleroad.domain.popup.QPopUpDetail.popUpDetail;

public class PopUpDetailRepositoryImpl implements PopUpDetailRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PopUpDetailRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public PopUpQueryDto findPopUpDetail(Long popupId) {
        return queryFactory
                .select(new QPopUpQueryDto(
                        popUp.id,
                        popUp.popupKeyNo,
                        popUp.mainImageUrl,
                        popUpDetail.detailImageUrl,
                        popUp.popupCondition,
                        popUp.popUpSection,
                        popUp.popUpDataType,
                        popUp.isMovePopupDetail
                ))
                .from(popUpDetail)
                .innerJoin(popUpDetail.popUp,popUp)
                .where(popUp.isVisible.in(true)
                        .and(popUp.id.eq(popupId)))
                .fetchOne();
    }
}
