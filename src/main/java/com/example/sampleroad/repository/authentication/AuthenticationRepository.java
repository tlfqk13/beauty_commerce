package com.example.sampleroad.repository.authentication;

import com.example.sampleroad.domain.authentication.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Long>, AuthenticationRepositoryCustom {
}
