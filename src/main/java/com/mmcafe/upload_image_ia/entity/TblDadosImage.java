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

    @Column(length = 512)
    private String nome;

    @Column(length = 128)
    private String tipo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String url;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String conteudo;
}
