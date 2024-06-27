package com.example.sampleroad.common.config;

import com.example.sampleroad.common.interceptor.PrintRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final PrintRequestInterceptor printRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(printRequestInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error","/error-page");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry){
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/")
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
    }
}
