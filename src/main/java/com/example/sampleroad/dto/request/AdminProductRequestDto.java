package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdminProductRequestDto {
    private int productNo;
    private boolean isSample;

    public boolean getIsSample() {
        return isSample;
    }
}
