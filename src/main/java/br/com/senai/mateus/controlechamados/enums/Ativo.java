package br.com.senai.mateus.controlechamados.enums;

public enum Ativo {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    Ativo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
