package com.mmcafe.upload_image_ia.messaging;

import com.mmcafe.upload_image_ia.dto.DadosImageDTO;
import com.mmcafe.upload_image_ia.entity.TblDadosImage;
import com.mmcafe.upload_image_ia.repository.TblDadosImageRepository;
import com.mmcafe.upload_image_ia.service.GroqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ImageConsumerTest {

    @Mock
    private TblDadosImageRepository repository;

    @Mock
    private GroqService groqService;

    @InjectMocks
    private ImageConsumer imageConsumer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsume_enrichesDescriptionAndSavesEntity() {
        // Arrange
        DadosImageDTO dto = new DadosImageDTO();
        dto.setNome("Imagem Teste");
        dto.setTipo("produto");
        dto.setDescricao("Uma imagem simples");
        dto.setUrl("http://image-url.com");

        String descricaoEnriquecida = "Texto enriquecido pela IA";
        when(groqService.enriquecerTexto(dto.getDescricao())).thenReturn(descricaoEnriquecida);

        // Act
        imageConsumer.consume(dto);

        // Assert
        ArgumentCaptor<TblDadosImage> captor = ArgumentCaptor.forClass(TblDadosImage.class);
        verify(repository).save(captor.capture());

        TblDadosImage savedEntity = captor.getValue();
        assert savedEntity.getNome().equals(dto.getNome());
        assert savedEntity.getTipo().equals(dto.getTipo());
        assert savedEntity.getDescricao().equals(dto.getDescricao());
        assert savedEntity.getUrl().equals(dto.getUrl());
        assert savedEntity.getConteudo().equals(descricaoEnriquecida);
    }

    @Test
    void testConsume_whenGroqFails_shouldNotThrowAndLog() {
        DadosImageDTO dto = new DadosImageDTO();
        dto.setDescricao("Falha simulada");

        when(groqService.enriquecerTexto(anyString()))
                .thenThrow(new RuntimeException("Erro de IA"));

        // Não deve lançar exceção
        assertDoesNotThrow(() -> imageConsumer.consume(dto));

        // Verifica que repository.save() NÃO foi chamado
        verify(repository, never()).save(any());
    }

}
