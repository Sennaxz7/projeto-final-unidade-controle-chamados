package br.com.senai.mateus.controlechamados.enums;

public enum Prioridade {
    BAIXA("Baixa"),
    MEDIA("Media"),
    ALTA("Alta");

    private final String descricao;
    Prioridade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
