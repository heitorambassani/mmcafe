package com.mmcafe.upload_image_ia.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.url}")
    private String cloudinaryURL;

    @Bean
    public Cloudinary cloudinary(@Value("${cloudinary.url}") String url) {
        Cloudinary c = new Cloudinary(url.trim());
        System.out.println("Cloudinary cloudName = " + c.config.cloudName); // deve imprimir drwnftxfb5
        return c;
    }

}