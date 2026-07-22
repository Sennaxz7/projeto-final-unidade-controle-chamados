package br.com.senai.mateus.controlechamados.dto;

import br.com.senai.mateus.controlechamados.entity.Chamado;
import br.com.senai.mateus.controlechamados.enums.Ativo;

import java.util.List;

public class TecnicoResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String especialidade;
    private Ativo ativo;

    public TecnicoResponseDTO() {}

    public TecnicoResponseDTO(Long id, String nome, String email, String especialidade, Ativo ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.especialidade = especialidade;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
