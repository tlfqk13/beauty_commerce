package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.RefreshToken;
import com.example.sampleroad.dto.request.MemberRequestDto;
import com.example.sampleroad.jwt.JwtTokenProvider;
import com.example.sampleroad.repository.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Transactional
    public HashMap<String, Object> refreshToken(MemberRequestDto.RefreshTokenUpdate dto) {
        HashMap<String, Object> resultMap = new HashMap<>();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(dto.getRefreshToken())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));

        boolean isValidateToken = refreshToken.isValid(dto.getRefreshToken());
        String validateToken = refreshToken.getToken().substring(7);

        if (isValidateToken && !validateRefreshToken(validateToken)) {
            throw new ErrorCustomException(ErrorCode.REFRESH_TOKEN_EXPIRE);
        }

        String accessToken = "Bearer " + jwtTokenProvider
                .createAccessToken(String.valueOf(refreshToken.getMember().getId()), refreshToken.getMember().getNickname());

        resultMap.put("accessToken", accessToken);
        resultMap.put("refreshToken", refreshToken.getToken());

        return resultMap;
    }

    private boolean validateRefreshToken(String refreshToken) {
        return jwtTokenProvider.validateToken(refreshToken);
    }
}
