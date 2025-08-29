package com.mmcafe.upload_image_ia.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mmcafe.upload_image_ia.dto.DadosImageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@Tag(name = "Upload de Imagens", description = "Envia imagem ao Cloudinary e publica metadados no RabbitMQ")
public class UploadController {

    private final Cloudinary cloudinary;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.default-receive-queue:image.queue}")
    public String queueName;

    public UploadController(Cloudinary cloudinary, RabbitTemplate rabbitTemplate) {
        this.cloudinary = cloudinary;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Operation(
            summary = "Envia uma imagem",
            description = "Faz upload para o Cloudinary e publica um **DadosImageDTO** na fila configurada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upload concluído"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadImage(
            @Parameter(
                    description = "Arquivo da imagem",
                    required = true,
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("imagem") MultipartFile imagem,

            @Parameter(description = "Nome da imagem", required = true)
            @RequestPart("nome") String nome,

            @Parameter(description = "Tipo/categoria", required = true)
            @RequestPart("tipo") String tipo,

            @Parameter(description = "Descrição", required = true)
            @RequestPart("descricao") String descricao
    ) throws IOException {

        try {
            Map uploadResult = cloudinary.uploader().upload(imagem.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            DadosImageDTO dto = new DadosImageDTO();
            dto.setNome(nome);
            dto.setTipo(tipo);
            dto.setDescricao(descricao);
            dto.setUrl(imageUrl);

            rabbitTemplate.convertAndSend(queueName, dto);

            return ResponseEntity.ok("Imagem enviada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao enviar imagem: " + e.getMessage());
        }
    }

}
