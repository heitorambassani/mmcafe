package com.mmcafe.upload_image_ia.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmcafe.upload_image_ia.config.GroqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService {

    private final GroqProperties properties;

    public String enriquecerTexto(String descricaoOriginal) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.groq.com/openai/v1/responses");
            post.setHeader("Authorization", "Bearer " + properties.getApiKey());
            post.setHeader("Content-Type", "application/json");

            String payload = """
        {
          "model": "%s",
          "input": "%s"
        }
        """.formatted(properties.getModel(), descricaoOriginal);

            post.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.getEntity().getContent());

                JsonNode outputArray = json.get("output");
                if (outputArray != null && outputArray.isArray()) {
                    for (JsonNode output : outputArray) {
                        if (output.has("content")) {
                            JsonNode contentArray = output.get("content");
                            if (contentArray.isArray()) {
                                for (JsonNode contentItem : contentArray) {
                                    if (contentItem.has("type") && "output_text".equals(contentItem.get("type").asText())) {
                                        return contentItem.get("text").asText();
                                    }
                                }
                            }
                        }
                    }
                }

                log.error("Resposta inválida da IA da Groq: {}", json.toPrettyString());
                return descricaoOriginal;
            }
        } catch (Exception e) {
            log.error("Erro ao consultar IA da Groq", e);
            return descricaoOriginal;
        }
    }



}