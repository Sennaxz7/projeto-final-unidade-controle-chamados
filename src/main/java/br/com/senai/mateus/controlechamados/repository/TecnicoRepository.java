package br.com.senai.mateus.controlechamados.repository;

import br.com.senai.mateus.controlechamados.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
}
