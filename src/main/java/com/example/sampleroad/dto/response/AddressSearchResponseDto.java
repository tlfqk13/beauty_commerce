package com.example.sampleroad.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class AddressSearchResponseDto {

    private int totalCount;
    private List<AddressSearch> items;

    public AddressSearchResponseDto(int totalCount, List<AddressSearch> items){
        this.totalCount = totalCount;
        this.items = items;
    }

    @NoArgsConstructor
    @Getter
    public static class AddressSearch {
        private String address;
        private String roadAddress;
        private String jibunAddress;
        private String zipCode;

        public AddressSearch(String address, String roadAddress, String jibunAddress, String zipCode){
            this.address = address;
            this.roadAddress = roadAddress;
            this.jibunAddress = jibunAddress;
            this.zipCode = zipCode;
        }
    }
}
