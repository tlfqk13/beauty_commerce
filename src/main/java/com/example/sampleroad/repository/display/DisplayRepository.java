package com.example.sampleroad.repository.display;

import com.example.sampleroad.domain.display.Display;
import com.example.sampleroad.domain.display.DisplayDesignType;
import com.example.sampleroad.domain.display.DisplayType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisplayRepository extends JpaRepository<Display, Long> {
    Optional<Display> findByDisplayNo(int displayNo);

    List<Display> findByIsVisible(boolean isVisible);

    List<Display> findByIsVisibleAndDisplayTypeIn(boolean isVisible, List<DisplayType> displayTypeList);

    Optional<Display> findByDisplayDesignTypeAndIsVisible(DisplayDesignType displayDesignType, boolean isVisible);
}
