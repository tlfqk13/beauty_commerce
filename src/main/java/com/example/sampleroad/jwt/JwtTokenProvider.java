package com.example.sampleroad.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    String secret;

    private Key key;

    // 토큰 유효시간 shopBy AccessToken 유효시간과 동일하게
    //private static final long REFRESH_TOKEN_VALID_TIME = 7776000 * 1000L; //90일
    private static final long REFRESH_TOKEN_VALID_TIME = 7776000 * 1000L; //90일
    //private static final long ACCESS_TOKEN_VALID_TIME = 60 * 1000L * 10;
    private static final long ACCESS_TOKEN_VALID_TIME = 7776000 * 1000L;

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성
    public String createAccessToken(String userPk, String name) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보 단위
        claims.put("name", name);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME)) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 signature에 들어갈 secret 값 설정
                .compact();
    }

    public String createRefreshToken(String userPk, String name) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보 단위
        claims.put("name", name);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME)) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 signature에 들어갈 secret 값 설정
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "TOKEN" : "TOKEN값"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰의 유효성 + 만료일자 확인  // -> 토큰이 expire되지 않았는지 True/False로 반환해줌.
    public boolean validateToken(ServletRequest request, String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}