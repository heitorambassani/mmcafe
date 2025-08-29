package com.mmcafe.upload_image_ia.config;

import com.mmcafe.upload_image_ia.properties.GroqProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GroqProperties.class)
public class AppConfig {
}
