package br.com.senai.mateus.controlechamados.dto;

import br.com.senai.mateus.controlechamados.enums.Ativo;

public class TecnicoRequestDTO {
    private String nome;
    private String email;
    private String especialidade;
    private Ativo ativo;

    public TecnicoRequestDTO() {}
    public TecnicoRequestDTO(String nome, String email, String especialidade, Ativo ativo) {
        this.nome = nome;
        this.email = email;
        this.especialidade = especialidade;
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public Ativo getAtivo() {
        return ativo;
    }

    public void setAtivo(Ativo ativo) {
        this.ativo = ativo;
    }
}
