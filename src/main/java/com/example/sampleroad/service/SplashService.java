package com.example.sampleroad.service;

import com.example.sampleroad.domain.splash.Splash;
import com.example.sampleroad.domain.splash.SplashType;
import com.example.sampleroad.dto.response.splash.SplashResponseDto;
import com.example.sampleroad.repository.splash.SplashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SplashService {

    private final SplashRepository splashRepository;

    public SplashResponseDto getSplash() {
        List<Splash> splashList = splashRepository.findByIsVisible(true);

        if (!splashList.isEmpty()) {
            List<Splash> mainSplashList = new ArrayList<>();
            List<Splash> adSplashList = new ArrayList<>();

            splashList.forEach(splash -> {
                if (splash.getSplashType().equals(SplashType.MAIN)) {
                    mainSplashList.add(splash);
                } else if (splash.getSplashType().equals(SplashType.AD)) {
                    adSplashList.add(splash);
                }
            });

            Random random = new Random();
            Splash randomSplash = adSplashList.get(random.nextInt(adSplashList.size()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            SplashResponseDto.AdSplash adSplash = new SplashResponseDto.AdSplash(
                    randomSplash.getId(), randomSplash.getSplashImgUrl(), randomSplash.getEndDate().format(formatter)
            );

            SplashResponseDto.MainSplash mainSplash = new SplashResponseDto.MainSplash(
                    mainSplashList.get(0).getSplashImgUrl()
            );

            return new SplashResponseDto(mainSplash, adSplash);

        } else {
            return new SplashResponseDto();
        }
    }
}

