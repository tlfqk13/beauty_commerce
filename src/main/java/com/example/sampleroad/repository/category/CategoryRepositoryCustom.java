package com.example.sampleroad.repository.category;

import com.example.sampleroad.domain.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findCategoriesByDepthNumbers(List<Integer> categoryNos,boolean isGetDepth2);
}
