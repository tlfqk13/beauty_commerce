package com.example.sampleroad.jwt;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // SC_UNAUTHORIZED = 401
            json.put("message", "로그인을 다시 해주세요 감사합니다 !!");
            json.put("status", 401);
            json.put("code", "A002");
            response.getWriter().print(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}