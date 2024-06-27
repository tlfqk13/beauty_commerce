package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class VersionRequestDto {
    private String version;
    private String os;
}
