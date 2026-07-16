package com.autotrader.backend.config.webconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.directory}")
    private String uploadDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
        /*
        addResourcehandler("/uploads/**") - Every request beginning with
        /uploads/ should use this resource handler.
        E.g.
        /uploads/car.jpg , /uploads/abc.jpg , /uploads/vehicles/toyota.webp

        addResorceLocations tells spring where to look for those files.
         */
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }

}
