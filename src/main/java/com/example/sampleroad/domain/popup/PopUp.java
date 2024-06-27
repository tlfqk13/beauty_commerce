package com.example.sampleroad.domain.popup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "POPUP")
public class PopUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POPUP_ID")
    private Long id;

    @Column(name = "MAIN_IMAGE_URL")
    @Lob
    private String mainImageUrl;

    @Column(name = "POPUP_KEY_NO")
    private int popupKeyNo;

    @Column(name = "POPUP_CONDITION")
    private String popupCondition;

    @Column(name = "IS_MOVE_POPUP_DETAIL")
    private boolean isMovePopupDetail;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    @Column(name = "EXTERNAL_URL")
    private String externalUrl;

    @Column(name = "POPUP_SECTION")
    @Enumerated(EnumType.STRING)
    private PopUpSectionType popUpSection;

    @Column(name = "POPUP_DATA_TYPE")
    @Enumerated(EnumType.STRING)
    private PopUpDataType popUpDataType;

    public boolean getIsVisible() {
        return isVisible;
    }

    public boolean getIsMovePopupDetail() {
        return isMovePopupDetail;
    }
}
