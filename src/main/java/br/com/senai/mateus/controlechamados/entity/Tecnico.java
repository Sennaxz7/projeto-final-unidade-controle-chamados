package br.com.senai.mateus.controlechamados.entity;

import br.com.senai.mateus.controlechamados.enums.Ativo;
import jakarta.persistence.*;

import java.util.List;

@Entity

public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String nome;
    private String email;
    private String especialidade;

    @Enumerated(EnumType.STRING)
    private Ativo ativo = Ativo.ATIVO;

    @ManyToMany(mappedBy = "tecnicos")
    private List<Chamado> chamados;

    public Tecnico(){}
    public Tecnico (Long id, String nome, String email, String especialidade, Ativo ativo,List<Chamado> chamados) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.especialidade = especialidade;
        this.ativo = ativo;
        this.chamados = chamados;
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

    public List<Chamado> getChamados() {
        return chamados;
    }

    public void setChamados(List<Chamado> chamados) {
        this.chamados = chamados;
    }
}
