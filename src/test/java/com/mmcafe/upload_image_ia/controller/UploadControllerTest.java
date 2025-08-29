package com.mmcafe.upload_image_ia.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.mmcafe.upload_image_ia.dto.DadosImageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadControllerTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private UploadController uploadController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void testUploadImage_success() throws Exception {
        // Mock da imagem
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "foto.png", "image/png", "conteudo".getBytes()
        );

        // Resposta mockada do Cloudinary
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://cloudinary.com/foto.png");
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        // Definir fila padrão manualmente (já que não vem do application.properties no teste)
        uploadController = new UploadController(cloudinary, rabbitTemplate);
        uploadController.queueName = "image.queue";

        // Executa
        ResponseEntity<String> response = uploadController.uploadImage(
                imagem, "nome da imagem", "categoria", "descrição"
        );

        // Verificações
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Imagem enviada com sucesso!", response.getBody());
        verify(rabbitTemplate).convertAndSend(eq("image.queue"), any(DadosImageDTO.class));
    }

    @Test
    void testUploadImage_cloudinaryFailure() throws Exception {
        // Mock da imagem
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "foto.png", "image/png", "conteudo".getBytes()
        );

        // Simular falha no upload
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new RuntimeException("Erro Cloudinary"));

        uploadController.queueName = "image.queue";

        // Executa
        ResponseEntity<String> response = uploadController.uploadImage(
                imagem, "nome da imagem", "categoria", "descrição"
        );

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Erro ao enviar imagem"));
    }


}
