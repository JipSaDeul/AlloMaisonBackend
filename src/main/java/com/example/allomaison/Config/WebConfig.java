package com.example.allomaison.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取 uploads/avatars 和 uploads/files 的绝对路径 URI
        String basePath = System.getProperty("user.dir") + "/uploads/";

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + basePath + "avatars/");

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + basePath + "files/");

    }
}
