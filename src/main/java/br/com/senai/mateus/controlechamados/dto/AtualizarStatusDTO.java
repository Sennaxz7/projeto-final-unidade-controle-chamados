package br.com.senai.mateus.controlechamados.dto;

import br.com.senai.mateus.controlechamados.enums.StatusChamado;

public class AtualizarStatusDTO {
    private StatusChamado statusChamado;

    public AtualizarStatusDTO() {
    }

    public StatusChamado getStatusChamado() {
        return statusChamado;
    }

    public void setStatusChamado(StatusChamado statusChamado) {
        this.statusChamado = statusChamado;
    }
}
