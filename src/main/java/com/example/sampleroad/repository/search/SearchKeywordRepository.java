package com.example.sampleroad.repository.search;

import com.example.sampleroad.domain.search.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword,Long>, SearchKeywordRepositoryCustom {
    Optional<SearchKeyword> findBySearchWord(String searchKeyword);
}
