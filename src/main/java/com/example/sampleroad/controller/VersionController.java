package com.example.sampleroad.controller;

import com.example.sampleroad.dto.request.VersionRequestDto;
import com.example.sampleroad.service.VersionService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class VersionController {

    private final VersionService versionService;

    @PostMapping("/version-check")
    @ApiOperation(value = "버전 체크 api")
    public void checkVersion(@RequestBody VersionRequestDto requestDto){
        versionService.checkVersion(requestDto);
    }
}
