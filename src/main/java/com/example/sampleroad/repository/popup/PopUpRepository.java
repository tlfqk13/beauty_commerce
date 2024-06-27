package com.example.sampleroad.repository.popup;

import com.example.sampleroad.domain.popup.PopUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopUpRepository extends JpaRepository<PopUp, Long> {
    List<PopUp> findByIsVisible(boolean isVisible);

}
