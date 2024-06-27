package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.response.ImageResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.ImageService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"이미지 관련 api Controller"})
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/api/image")
    @ResponseBody
    public ResponseEntity<ResultInfo> addImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @RequestParam("imageFile") List<MultipartFile> imageFile) throws UnirestException, ParseException, IOException {

        for (MultipartFile file : imageFile) {
            long fileSize = file.getSize();
            System.out.println("파일명: " + file.getOriginalFilename() + ", 용량: " + fileSize + " bytes");
        }

        ImageResponseDto imageResponseDto = imageService.imageAdd(userDetails, imageFile);

        ResultInfo resultInfo = new ResultInfo(ResultInfo.Code.CREATED, "이미지 등록 완료", imageResponseDto);
        return new ResponseEntity<>(resultInfo, HttpStatus.ACCEPTED);
    }
}
