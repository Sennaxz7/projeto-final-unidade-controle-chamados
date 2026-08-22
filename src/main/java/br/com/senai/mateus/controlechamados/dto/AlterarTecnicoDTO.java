package br.com.senai.mateus.controlechamados.dto;

import java.util.List;

public class AlterarTecnicoDTO {
    private List<Long> tecnicosIds;

    public AlterarTecnicoDTO() {
    }

    public List<Long> getTecnicosIds() {
        return tecnicosIds;
    }

    public void setTecnicosIds(List<Long> tecnicosIds) {
        this.tecnicosIds = tecnicosIds;
    }
}
