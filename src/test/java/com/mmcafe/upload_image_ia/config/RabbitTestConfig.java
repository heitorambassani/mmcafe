package com.mmcafe.upload_image_ia.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class RabbitTestConfig {

    @Bean
    public RabbitTemplate rabbitTemplate() {
        // Mock simples só para satisfazer as dependências nos testes
        return mock(RabbitTemplate.class);
    }
}