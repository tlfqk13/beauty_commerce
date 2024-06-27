package com.example.sampleroad.repository.search;

import com.example.sampleroad.dto.response.search.SearchKeywordQueryDto;

import java.util.List;

public interface SearchKeywordRepositoryCustom  {
    List<SearchKeywordQueryDto> findSearchKeywordByIsVisbile();
}
