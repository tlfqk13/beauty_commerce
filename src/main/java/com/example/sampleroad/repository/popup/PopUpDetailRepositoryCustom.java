package com.example.sampleroad.repository.popup;

import com.example.sampleroad.dto.response.PopUpQueryDto;

public interface PopUpDetailRepositoryCustom {
    PopUpQueryDto findPopUpDetail(Long popupId);
}
