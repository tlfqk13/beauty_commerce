package com.example.sampleroad.repository.product;

import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.dto.response.product.EventProductQueryDto;

import java.util.List;

public interface EventProductRepositoryCustom {
    List<EventProductQueryDto.EventProductInfo> findEventProduct(List<Integer> productNoList);
    List<EventProductQueryDto.EventProductInfo> findPriceZeroEventProduct(List<Integer> customKitProductOptionNos);
    List<EventProductQueryDto.EventProductInfo> findEventProductByIsVisible(EventProductType eventProductType);
}
