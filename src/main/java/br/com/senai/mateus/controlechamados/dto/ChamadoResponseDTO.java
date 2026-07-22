package br.com.senai.mateus.controlechamados.dto;

import br.com.senai.mateus.controlechamados.enums.Prioridade;
import br.com.senai.mateus.controlechamados.enums.StatusChamado;

import java.time.LocalDate;
import java.util.List;

public class ChamadoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String solicitante;
    private String local;
    private Prioridade prioridade;
    private StatusChamado status;
    private LocalDate dataAbertura;
    private LocalDate dataFinalizacao;
    private CategoriaResponseDTO categoria;
    private List<TecnicoResponseDTO> tecnicos;

    public ChamadoResponseDTO(){}

    public ChamadoResponseDTO(Long id, String titulo, String descricao, String solicitante, String local,
                              Prioridade prioridade, StatusChamado status, LocalDate dataAbertura,
                              LocalDate dataFinalizacao, CategoriaResponseDTO categoria, List<TecnicoResponseDTO> tecnicos) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.local = local;
        this.prioridade = prioridade;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataFinalizacao = dataFinalizacao;
        this.categoria = categoria;
        this.tecnicos = tecnicos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusChamado getStatus() {
        return status;
    }

    public void setStatus(StatusChamado status) {
        this.status = status;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDate getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDate dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public CategoriaResponseDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponseDTO categoria) {
        this.categoria = categoria;
    }

    public List<TecnicoResponseDTO> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<TecnicoResponseDTO> tecnicos) {
        this.tecnicos = tecnicos;
    }
}
