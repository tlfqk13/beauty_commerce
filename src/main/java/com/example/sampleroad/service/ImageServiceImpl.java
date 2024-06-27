package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.Image;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.dto.response.ImageResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.ImageRepository;

import org.json.JSONException;
import org.json.simple.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Override
    @Transactional
    public ImageResponseDto imageAdd(UserDetailsImpl userDetails, List<MultipartFile> imageFile) {

        List<ImageResponseDto.ImageUrl> imageResponseDtos = new ArrayList<>();

        try {
            for (MultipartFile multipartFile : imageFile) {
                JSONObject jsonObject = shopbyImageAdd(multipartFile);
                String originalFilename = multipartFile.getOriginalFilename();
                String imageUrl = jsonObject.get("imageUrl").toString();
                imageUrl = "https:" + imageUrl;
                Member member = userDetails.getMember();
                Image image = makeImage(imageUrl, originalFilename, member);
                imageRepository.save(image);
                ImageResponseDto.ImageUrl imageUrls = new ImageResponseDto.ImageUrl(imageUrl);
                imageResponseDtos.add(imageUrls);
            }
        } catch (JSONException | UnirestException | ParseException | IOException e) {
            throw new ErrorCustomException(ErrorCode.IMAGE_FIELD_UPLOAD_FAIL);
        }

        return new ImageResponseDto(imageResponseDtos);
    }

    private Image makeImage(String imageUrl, String originName, Member member) {
        return Image.builder()
                .imageUrl(imageUrl)
                .originName(originName)
                .member(member)
                .build();
    }

    private JSONObject shopbyImageAdd(MultipartFile imageFile) throws UnirestException, ParseException, IOException {
        HttpResponse<String> response = Unirest.post(shopByUrl + "/files/images")
                .header("version", "1.0")
                .header("clientid", clientId)
                .header("platform", "PC")
                .field("file", imageFile.getInputStream(), imageFile.getOriginalFilename())
                .asString();
        return ShopBy.errorMessage(response);
    }
}