package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ProductRequestDto {

    @NoArgsConstructor
    @Getter
    public static class RecentProducts {

        List<Integer> recentProductNos;

    }
}
