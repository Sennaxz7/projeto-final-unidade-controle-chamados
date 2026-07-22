package br.com.senai.mateus.controlechamados.dto;

import java.util.List;

public class VincularTecnicoDTO {
    private List<Long> tecnicosIds;

    public VincularTecnicoDTO() {
    }

    public List<Long> getTecnicosIds() {
        return tecnicosIds;
    }

    public void setTecnicosIds(List<Long> tecnicosIds) {
        this.tecnicosIds = tecnicosIds;
    }
}
