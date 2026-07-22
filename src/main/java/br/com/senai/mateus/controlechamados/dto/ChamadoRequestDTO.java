package br.com.senai.mateus.controlechamados.dto;

import br.com.senai.mateus.controlechamados.enums.Prioridade;

import java.util.List;

public class ChamadoRequestDTO {
    private String titulo;
    private String descricao;
    private String solicitante;
    private String local;
    private Prioridade prioridade;
    private Long categoriaId;
    private List<Long> tecnicosIds;

    public ChamadoRequestDTO(){}

    public ChamadoRequestDTO(String titulo, String descricao, String solicitante, String local,
                             Prioridade prioridade, Long categoriaId, List<Long> tecnicosIds) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.local = local;
        this.prioridade = prioridade;
        this.categoriaId = categoriaId;
        this.tecnicosIds = tecnicosIds;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public List<Long> getTecnicosIds() {
        return tecnicosIds;
    }

    public void setTecnicosIds(List<Long> tecnicosIds) {
        this.tecnicosIds = tecnicosIds;
    }
}
