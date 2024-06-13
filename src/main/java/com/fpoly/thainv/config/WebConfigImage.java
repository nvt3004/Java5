package com.fpoly.thainv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigImage implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/Admin/**")
                .addResourceLocations("file:static/Admin/");
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:static/images/");
    }
}