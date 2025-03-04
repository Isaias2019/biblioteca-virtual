package com.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 60, message = "O título deve ter no máximo 60 caracteres")
    private String titulo;
    
    @NotBlank(message = "O autor é obrigatório")
    @Size(max = 30, message = "O autor deve ter no máximo 30 caracteres")
    private String autor;
    
    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 80, message = "A descrição deve ter no máximo 80 caracteres")
    private String descricao;
    
    private String nomeArquivo;
    private String tipoArquivo;
    
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] capa;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] conteudo;
} 