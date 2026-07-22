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
    private List<Chamado> chamados;
}
