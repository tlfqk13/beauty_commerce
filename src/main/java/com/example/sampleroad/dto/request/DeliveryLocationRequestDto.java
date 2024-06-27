package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.DeliveryLocation;
import com.example.sampleroad.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeliveryLocationRequestDto {
    @NoArgsConstructor
    @Getter
    public static class CreateDeliveryLocation {
        private boolean defaultAddress;
        private String receiverName;
        private String addressName;
        private String receiverContact;
        private String receiverZipCode;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;

        public CreateDeliveryLocation(ExperienceRequestDto dto) {
            this.defaultAddress = true;
            this.receiverName = dto.getReceiverName();
            this.receiverContact = dto.getReceiverContact();
            this.receiverZipCode = dto.getReceiverZipCode();
            this.receiverAddress = dto.getReceiverDetailAddress();
            this.receiverDetailAddress = dto.getReceiverDetailAddress();
        }

        public boolean getDefaultAddress() {
            return defaultAddress;
        }

        public DeliveryLocation toEntity(Member member) {
            return DeliveryLocation.builder()
                    .defaultAddress(defaultAddress)
                    .receiverName(receiverName)
                    .addressName(addressName)
                    .receiverContact(receiverContact)
                    .receiverZipCode(receiverZipCode)
                    .receiverAddress(receiverAddress)
                    .receiverJibunAddress(receiverJibunAddress)
                    .receiverDetailAddress(receiverDetailAddress)
                    .member(member)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Update {
        private boolean defaultAddress;
        private String receiverName;
        private String addressName;
        private String receiverContact;
        private String receiverZipCode;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;

        public boolean getDefaultAddress() {
            return defaultAddress;
        }
    }


}
