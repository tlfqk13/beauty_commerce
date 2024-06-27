package com.example.sampleroad.repository.samplekit;

import com.example.sampleroad.dto.response.sampleKit.SampleKitQueryDto;

import java.util.List;

public interface SampleKitRepositoryCustom {

    List<SampleKitQueryDto.SampleKit> findSampleKitByProductNoIn(List<Integer> productNos);
}
