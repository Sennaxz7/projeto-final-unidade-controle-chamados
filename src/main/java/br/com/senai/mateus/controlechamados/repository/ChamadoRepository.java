package br.com.senai.mateus.controlechamados.repository;

import br.com.senai.mateus.controlechamados.entity.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
}
