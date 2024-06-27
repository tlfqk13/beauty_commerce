package com.example.sampleroad.controller;

import com.example.sampleroad.dto.response.display.DisplayResponseDto;
import com.example.sampleroad.service.DisplayService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"기획전 관련 api Controller"})
@RequiredArgsConstructor
public class DisplayController {

    private final DisplayService displayService;

    @GetMapping("/api/display/events")
    @ApiOperation(value = "기획전 조회")
    public DisplayResponseDto.DisplayList getDisplayEvents() throws UnirestException, ParseException {
        return displayService.getDisplayEvents();
    }

    @GetMapping("/api/display/events/{eventNo}")
    @ApiOperation(value = "기획전 상세 조회")
    public DisplayResponseDto getDisplayEventDetail(@PathVariable int eventNo) throws UnirestException, ParseException {
        return displayService.getDisplayEventDetail(eventNo);
    }
}
