package com.mmcafe.upload_image_ia.service;

import com.mmcafe.upload_image_ia.properties.GroqProperties;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GroqServiceTest {

    private GroqProperties properties;
    private GroqService service;

    @BeforeEach
    void setup() {
        properties = new GroqProperties();
        properties.setApiKey("fake-key");
        properties.setModel("fake-model");

        service = new GroqService(properties);
    }

    @Test
    void deveRetornarTextoEnriquecidoComRespostaValidaDaIA() throws Exception {
        String fakeResponse = """
        {
          "output": [
            {
              "content": [
                {
                  "type": "output_text",
                  "text": "Texto enriquecido pela IA"
                }
              ]
            }
          ]
        }
        """;

        try (MockedStatic<HttpClients> httpClientsMockedStatic = mockStatic(HttpClients.class)) {
            // Mock correto
            CloseableHttpClient mockClient = mock(CloseableHttpClient.class);
            CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
            HttpEntity entity = new StringEntity(fakeResponse, StandardCharsets.UTF_8);

            when(mockResponse.getEntity()).thenReturn(entity);
            when(mockClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
            httpClientsMockedStatic.when(HttpClients::createDefault).thenReturn(mockClient);

            String result = service.enriquecerTexto("descricao simples");
            assertEquals("Texto enriquecido pela IA", result);
        }
    }


    @Test
    void deveRetornarDescricaoOriginalEmCasoDeErro() throws Exception {
        // Simular exceção ao executar
        CloseableHttpClient mockClient = mock(CloseableHttpClient.class);
        when(mockClient.execute(any(HttpPost.class))).thenThrow(new RuntimeException("Erro de rede"));

        try (MockedStatic<HttpClients> mocked = Mockito.mockStatic(HttpClients.class)) {
            mocked.when(HttpClients::createDefault).thenReturn(mockClient);

            String entrada = "descricao original";
            String resultado = service.enriquecerTexto(entrada);

            assertEquals(entrada, resultado); // Deve retornar a descrição original
        }
    }

    @Test
    void deveRetornarDescricaoOriginalQuandoRespostaInvalida() throws Exception {
        // JSON sem o campo esperado
        String fakeResponse = """
            {
              "foo": "bar"
            }
            """;

        CloseableHttpClient mockClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        HttpEntity mockEntity = new StringEntity(fakeResponse, StandardCharsets.UTF_8);

        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        try (MockedStatic<HttpClients> mocked = Mockito.mockStatic(HttpClients.class)) {
            mocked.when(HttpClients::createDefault).thenReturn(mockClient);

            String entrada = "minha descrição";
            String resultado = service.enriquecerTexto(entrada);

            assertEquals(entrada, resultado); // Deve retornar a original por não encontrar o texto
        }
    }
}
