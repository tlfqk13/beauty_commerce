package com.example.sampleroad.dto.response.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SearchKeywordQueryDto {
    private String searchKeyword;

    @QueryProjection
    public SearchKeywordQueryDto(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
