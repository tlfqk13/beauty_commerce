package com.example.sampleroad.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String token = request.getHeader("Authorization");

        // 토큰이 존재하며 비어 있지 않은지 확인
        if (token != null && !token.isEmpty()) {
            boolean validToken = jwtTokenProvider.validateToken(token.substring(7));
            if (!validToken) {
                // 오류 메시지를 담은 JSON 객체를 준비
                JSONObject json = new JSONObject();
                json.put("message", "로그인을 다시 해주세요. 감사합니다!!");
                json.put("status", 401);
                json.put("code", "A002");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(json);
                return; // 추가 처리 중단
            }
        }

        // 토큰이 없거나 유효한 경우, 필터 체인을 계속 진행
        filterChain.doFilter(request, response);
    }
}