package com.example.sampleroad.dto.response.customkit;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CustomKitItemQueryDto {

    private Long customKitItemId;
    private Long customKitId;
    private int orderCnt;
    private int productOptionNo;
    private int productNo;
    private String orderNo;

    @QueryProjection
    public CustomKitItemQueryDto(Long customKitItemId, Long customKitId, int orderCnt, int productOptionNo,
                                 int productNo,String orderNo) {
        this.customKitItemId = customKitItemId;
        this.customKitId = customKitId;
        this.orderCnt = orderCnt;
        this.productOptionNo = productOptionNo;
        this.productNo = productNo;
        this.orderNo = orderNo;
    }
}
