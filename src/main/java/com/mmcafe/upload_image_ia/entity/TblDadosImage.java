package com.mmcafe.upload_image_ia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_dados_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblDadosImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String nome;

    @Column(length = 255)
    private String tipo;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 1000)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String conteudo;
}
