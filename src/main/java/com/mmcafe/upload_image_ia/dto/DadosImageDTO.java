package com.mmcafe.upload_image_ia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosImageDTO implements Serializable {
    private String nome;
    private String tipo;
    private String descricao;
    private String url;
}
