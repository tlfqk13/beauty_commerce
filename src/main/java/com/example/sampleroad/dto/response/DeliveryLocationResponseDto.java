package com.example.sampleroad.dto.response;

import com.example.sampleroad.dto.response.member.MemberQueryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class DeliveryLocationResponseDto {

    @NoArgsConstructor
    @Getter
    public static class DeliveryLocationRegister {
        private Long addressId;

        public DeliveryLocationRegister(Long addressId) {
            this.addressId = addressId;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DeliveryLocation {
        private Long addressId;
        private Boolean defaultAddress;
        private String receiverName;
        private String addressName;
        private String receiverContact;
        private String receiverZipCode;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;

        // 생성자 추가
        public DeliveryLocation(com.example.sampleroad.domain.DeliveryLocation deliveryLocation) {
            this.addressId = deliveryLocation.getId();
            this.defaultAddress = deliveryLocation.getDefaultAddress();
            this.receiverName = deliveryLocation.getReceiverName();
            this.addressName = deliveryLocation.getAddressName();
            this.receiverContact = deliveryLocation.getReceiverContact();
            this.receiverZipCode = deliveryLocation.getReceiverZipCode();
            this.receiverAddress = deliveryLocation.getReceiverAddress();
            this.receiverJibunAddress = deliveryLocation.getReceiverJibunAddress();
            this.receiverDetailAddress = deliveryLocation.getReceiverDetailAddress();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class AllDeliveryLocations {
        private int totalCount;
        private List<DeliveryLocation> addresses;

        public AllDeliveryLocations(int totalCount, List<DeliveryLocation> addresses) {
            this.totalCount = totalCount;
            this.addresses = addresses;
        }
    }



}
