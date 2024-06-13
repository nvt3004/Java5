package com.fpoly.thainv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Điều chỉnh đường dẫn phù hợp với cấu trúc thư mục thực tế của bạn
//        registry.addResourceHandler("/Admin/assets/**")
//                .addResourceLocations("file:../static/Admin/assets/**");
//        registry.addResourceHandler("/Client/assets/**")
//                .addResourceLocations("file:../static/Client/assets/**");
    }
}