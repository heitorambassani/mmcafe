package com.mmcafe.upload_image_ia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "image.queue";

    @Bean
    public Queue imageQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public MessageConverter jackson2MessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("com.mmcafe.upload_image_ia.dto");
        converter.setClassMapper(classMapper);
        return converter;
    }
}
