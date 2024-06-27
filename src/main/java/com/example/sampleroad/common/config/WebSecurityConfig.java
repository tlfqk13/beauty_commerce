package com.example.sampleroad.common.config;

import com.example.sampleroad.jwt.CustomAuthenticationFilter;
import com.example.sampleroad.jwt.JwtAuthenticationEntryPoint;
import com.example.sampleroad.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled  = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable
                .csrf().disable()
                .cors()
                .and()
                // TODO: 2023/11/09 운영 반영시 주석
                //.addFilterBefore(customAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/member/ci-check").permitAll()
                .antMatchers(HttpMethod.GET,"/api/member/login-id-check").permitAll()
                .antMatchers(HttpMethod.GET,"/api/member/nickname-check").permitAll()
                .antMatchers(HttpMethod.GET,"/api/member/email-check").permitAll()
                .antMatchers(HttpMethod.GET,"/certificate/**").permitAll()
                .antMatchers(HttpMethod.GET,"/oauth/**").permitAll()
                .antMatchers(HttpMethod.POST,"/oauth/**").permitAll()
                .antMatchers(HttpMethod.GET,"/profile/**").permitAll()
                .antMatchers(HttpMethod.POST,"/version-check/**").permitAll()
                .antMatchers(HttpMethod.POST,"/api/login").permitAll()
                .antMatchers(HttpMethod.POST,"/api/refresh").permitAll()
                .antMatchers(HttpMethod.POST,"/api/join").permitAll()
                .antMatchers(HttpMethod.POST,"/api/image").permitAll()
                .antMatchers(HttpMethod.POST,"/api/authentications/id").permitAll()
                .antMatchers(HttpMethod.GET,"/api/authentications").permitAll()
                .antMatchers("/payment/**").permitAll()
                .antMatchers("/api/splash").permitAll()
                .antMatchers("/api/admin/**").permitAll()
                .antMatchers("/api/push").permitAll()
                .antMatchers("/api/authentications/**").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui/**").permitAll()
                // TODO: 2023/11/09 운영 반영시 주석
                //.antMatchers("/api/home").permitAll()   // 비회원도 접근 가능
                //.antMatchers("/api/search/**").permitAll()   // 비회원도 접근 가능
                //.antMatchers("/api/product-info/**").permitAll()   // 비회원도 접근 가능
                .antMatchers("/api/product-recent").permitAll()   // 비회원도 접근 가능
                //.antMatchers("/api/custom-kit").permitAll()   // 비회원도 접근 가능
                //.antMatchers("/api/event/product").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfig(tokenProvider));
    }
}