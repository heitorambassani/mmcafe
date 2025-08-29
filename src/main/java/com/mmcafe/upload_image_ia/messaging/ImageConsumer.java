package com.mmcafe.upload_image_ia.messaging;

import com.mmcafe.upload_image_ia.config.RabbitConfig;
import com.mmcafe.upload_image_ia.dto.DadosImageDTO;
import com.mmcafe.upload_image_ia.entity.TblDadosImage;
import com.mmcafe.upload_image_ia.repository.TblDadosImageRepository;
import com.mmcafe.upload_image_ia.service.GroqService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageConsumer {

    private final TblDadosImageRepository repository;
    private final GroqService groqService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consume(DadosImageDTO dto) {
        try {
            String textoEnriquecido = groqService.enriquecerTexto(dto.getDescricao());

            TblDadosImage entity = new TblDadosImage();
            entity.setNome(dto.getNome());
            entity.setTipo(dto.getTipo());
            entity.setDescricao(dto.getDescricao());
            entity.setUrl(dto.getUrl());
            entity.setConteudo(textoEnriquecido);

            repository.save(entity);
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
