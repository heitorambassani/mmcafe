package com.mmcafe.upload_image_ia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Configuration
public class GroqConfig {

    @Value("${groq.api-key}")
    private String groqApiKey;

    @Bean
    public OkHttpClient groqClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + groqApiKey)
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }
}
