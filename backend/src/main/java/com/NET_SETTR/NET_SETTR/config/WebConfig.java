package com.NET_SETTR.NET_SETTR.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/SYLLABUS/**")
                .addResourceLocations("file:E:/NET-SETTR_PROJECT/DOC/SYLLABUS/");

        // (Optional but recommended)
        registry.addResourceHandler("/NOTES/**")
                .addResourceLocations("file:E:/NET-SETTR_PROJECT/DOC/NOTES/");
    }
}
