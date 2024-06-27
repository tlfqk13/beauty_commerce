package com.example.sampleroad.service;

import com.example.sampleroad.dto.response.ImageResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    ImageResponseDto imageAdd(UserDetailsImpl userDetails, List<MultipartFile> imageFile) throws UnirestException, ParseException, IOException;
}
