package br.com.senai.mateus.controlechamados.dto;

public class CategoriaRequestDTO {
    private String nome;
    private String descricao;

    public CategoriaRequestDTO() {}
    public CategoriaRequestDTO(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
