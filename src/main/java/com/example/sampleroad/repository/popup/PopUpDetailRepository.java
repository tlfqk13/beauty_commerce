package com.example.sampleroad.repository.popup;

import com.example.sampleroad.domain.popup.PopUpDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopUpDetailRepository extends JpaRepository<PopUpDetail, Long>, PopUpDetailRepositoryCustom {

}
