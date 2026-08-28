package br.com.senai.mateus.controlechamados.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true, nullable = false)
    private String nome;
    private String descricao;

    @OneToMany(mappedBy = "categoria")
    private List<Chamado> chamados;

    public Categoria() {}
    public Categoria(Long id, String nome, String descricao, List<Chamado> chamados) {
        this.id =id;
        this.nome = nome;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Chamado> getChamados() {
        return chamados;
    }

    public void setChamados(List<Chamado> chamados) {
        this.chamados = chamados;
    }
}
