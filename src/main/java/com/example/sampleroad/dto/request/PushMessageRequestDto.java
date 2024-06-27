package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.SkinType;
import com.example.sampleroad.domain.push.MemberType;
import com.example.sampleroad.domain.push.PushDataType;
import com.example.sampleroad.domain.push.PushType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class PushMessageRequestDto {

    @NoArgsConstructor
    @Getter
    public static class CreateToken {
        private String pushToken;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Send {
        private String title;
        private String body;
        private String image;
        private PushType pushType;
        private PushDataType pushDataType;
        private MemberType memberType;
        private SkinType skinType;
        private int productNo;
        private String orderNo;

        public Send(String title, String body, String image, PushType pushType, PushDataType pushDataType,
                    MemberType memberType, SkinType skinType, int productNo) {
            this.title = title;
            this.body = body;
            this.image = image;
            this.pushType = pushType;
            this.pushDataType = pushDataType;
            this.memberType = memberType;
            this.skinType = skinType;
            this.productNo = productNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Data {
        private Long productNo; // 상품상세 용도
    }
}
