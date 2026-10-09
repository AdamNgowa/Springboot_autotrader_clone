package com.autotrader.backend.config.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
           @Value("${cloudinary.url}") String cloudinaryUrl
    ) {
        return new Cloudinary(cloudinaryUrl);
    }
}