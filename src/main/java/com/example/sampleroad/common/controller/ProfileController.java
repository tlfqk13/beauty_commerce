package com.example.sampleroad.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ProfileController {
    private final Environment env;

    @GetMapping("/profile")
    public String profile() {
        log.info("CI/CD 무중단 배포");
        List<String> profiles = Arrays.asList(env.getActiveProfiles());
        List<String> realProfiles = Arrays.asList("real1", "real2");
        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

        // real, real1, real2 중 하나라도 있으면 그 값 반환
        return profiles.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }

    @GetMapping("/")
    public String test() {
        return "Test Page2";
    }
}