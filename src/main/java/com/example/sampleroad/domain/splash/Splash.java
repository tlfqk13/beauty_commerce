package com.example.sampleroad.domain.splash;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SPLASH")
public class Splash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPLASH_ID")
    private Long id;

    @Column(name = "SPLASH_IMG_URL")
    private String splashImgUrl;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "SPLASH_TYPE")
    @Enumerated(EnumType.STRING)
    private SplashType splashType;

    public boolean getIsVisible() {
        return isVisible;
    }
}
