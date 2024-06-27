package com.example.sampleroad.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdminProductResponseDto {

    private int productNo;
    private String productName;
    private String brandName;
    private int brandNo;
    private String imageUrl;
    private int categoryNo;

    public AdminProductResponseDto(int productNo, String productName, String brandName,
                                   int brandNo, String imageUrl, int categoryNo) {
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.brandNo = brandNo;
        this.imageUrl = imageUrl;
        this.categoryNo = categoryNo;
    }
}
