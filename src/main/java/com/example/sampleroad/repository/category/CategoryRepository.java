package com.example.sampleroad.repository.category;

import com.example.sampleroad.domain.Category;
import com.example.sampleroad.domain.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>,CategoryRepositoryCustom {
    Optional<Category> findByCategoryDepthNumber3(int categoryNumber);
    Optional<Category> findByCategoryDepthNumber1(int categoryNumber);
    Optional<Category> findByCategoryDepth2(CategoryType categoryType);
    List<Category> findByIsHomeVisibleAndHomeVisibleNumberNotNullOrderByHomeVisibleNumber(boolean isHomeVisible);

}
