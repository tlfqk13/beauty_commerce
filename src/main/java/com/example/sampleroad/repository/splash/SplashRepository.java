package com.example.sampleroad.repository.splash;

import com.example.sampleroad.domain.splash.Splash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SplashRepository extends JpaRepository<Splash, Long> {

    List<Splash> findByIsVisible(boolean isVisible);

}
