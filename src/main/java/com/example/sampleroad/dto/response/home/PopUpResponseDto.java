package com.example.sampleroad.dto.response.home;

import com.example.sampleroad.domain.popup.PopUpDataType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PopUpResponseDto {

    @NoArgsConstructor
    @Getter
    public static class PopUpInfo {

        private int popupNo;
        private String content;
        private Boolean isVisibleToday;

        public PopUpInfo(int popupNo, String content, Boolean isVisibleToday) {
            this.popupNo = popupNo;
            this.content = content;
            this.isVisibleToday = isVisibleToday;
        }

        public PopUpInfo(int popupNo, Boolean isVisibleToday) {
            this.popupNo = popupNo;
            this.isVisibleToday = isVisibleToday;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PopUpDetail {

        private Long popUpId;
        private String popUpDetailImgUrl;

        public PopUpDetail(Long popUpId, String popUpDetailImgUrl) {
            this.popUpId = popUpId;
            this.popUpDetailImgUrl = popUpDetailImgUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class HomePopUpInfo {

        private Long popupId;
        private String popupImgUrl;
        private int popupKeyNo;
        private PopUpDataType popUpDataType;
        private Boolean isMovePopupDetail;
        private String externalUrl;

        public HomePopUpInfo(Long popupId, String popupImgUrl, int popupKeyNo, PopUpDataType popUpDataType,
                             Boolean isMovePopupDetail, String externalUrl) {
            this.popupId = popupId;
            this.popupImgUrl = popupImgUrl;
            this.popupKeyNo = popupKeyNo;
            this.popUpDataType = popUpDataType;
            this.isMovePopupDetail = isMovePopupDetail;
            this.externalUrl = externalUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PouUpRelationBoard {
        private String popupDetailImgUrl;

        public PouUpRelationBoard(String popupDetailImgUrl) {
            this.popupDetailImgUrl = popupDetailImgUrl;
        }
    }

}
