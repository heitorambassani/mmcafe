package com.mmcafe.upload_image_ia.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "groq")
public class GroqProperties {
    private String apiKey;
    private String url;
    private String model;
    private long timeoutMs;
}
