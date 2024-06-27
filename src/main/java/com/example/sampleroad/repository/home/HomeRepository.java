package com.example.sampleroad.repository.home;

import com.example.sampleroad.domain.home.Home;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeRepository extends JpaRepository<Home,Long>, HomeRepositoryCustom {
}
