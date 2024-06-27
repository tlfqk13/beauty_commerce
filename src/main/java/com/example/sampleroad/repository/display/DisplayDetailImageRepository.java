package com.example.sampleroad.repository.display;

import com.example.sampleroad.domain.display.DisplayDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisplayDetailImageRepository extends JpaRepository<DisplayDetailImage, Long> {

    List<DisplayDetailImage> findByDisplay_DisplayNoOrderByImagePosAsc(int displayNo);
    
}
