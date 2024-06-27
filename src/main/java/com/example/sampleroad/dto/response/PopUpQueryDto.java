package com.example.sampleroad.dto.response;

import com.example.sampleroad.domain.popup.PopUpDataType;
import com.example.sampleroad.domain.popup.PopUpSectionType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PopUpQueryDto {
    Long popUpId;
    int popUpKeyNo;
    String popUpImageUrl;
    String popUpDetailImageUrl;
    String popUpCondition;
    PopUpSectionType popUpSectionType;
    PopUpDataType popUpDataType;
    Boolean isMovePopupDetail;

    @QueryProjection
    public PopUpQueryDto(Long popUpId,int popUpKeyNo,
                         String popUpImageUrl, String popUpDetailImageUrl,
                         String popUpCondition,
                         PopUpSectionType popUpSectionType, PopUpDataType popUpDataType,
                         Boolean isMovePopupDetail) {
        this.popUpId = popUpId;
        this.popUpKeyNo = popUpKeyNo;
        this.popUpImageUrl = popUpImageUrl;
        this.popUpDetailImageUrl = popUpDetailImageUrl;
        this.popUpCondition = popUpCondition;
        this.popUpSectionType = popUpSectionType;
        this.popUpDataType = popUpDataType;
        this.isMovePopupDetail = isMovePopupDetail;
    }
}
