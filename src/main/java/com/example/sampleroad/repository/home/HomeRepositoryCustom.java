package com.example.sampleroad.repository.home;

import com.example.sampleroad.domain.product.HomeProductType;
import com.example.sampleroad.dto.response.home.HomeProductResponseQueryDto;

import java.util.List;

public interface HomeRepositoryCustom {
    List<HomeProductResponseQueryDto> findHomeProductByHomeType(HomeProductType homeProductType);
}
