package br.com.senai.mateus.controlechamados.service;

import br.com.senai.mateus.controlechamados.dto.TecnicoRequestDTO;
import br.com.senai.mateus.controlechamados.dto.TecnicoResponseDTO;
import br.com.senai.mateus.controlechamados.entity.Tecnico;
import br.com.senai.mateus.controlechamados.enums.Ativo;
import br.com.senai.mateus.controlechamados.exception.RecursoNaoEncontradoException;
import br.com.senai.mateus.controlechamados.exception.RegraDeNegocioException;
import br.com.senai.mateus.controlechamados.repository.ChamadoRepository;
import br.com.senai.mateus.controlechamados.repository.TecnicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TecnicoService {
    private final TecnicoRepository tecnicoRepository;
    private final ChamadoRepository chamadoRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository, ChamadoRepository chamadoRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.chamadoRepository = chamadoRepository;
    }

    public List<TecnicoResponseDTO> buscar() {
        return tecnicoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public TecnicoResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarTecnicoPorId(id));
    }

    public TecnicoResponseDTO salvar(TecnicoRequestDTO requestDTO) {
        validarDados(requestDTO, null);
        Tecnico tecnico = new Tecnico();
        tecnico.setNome(requestDTO.getNome().trim());
        tecnico.setEmail(requestDTO.getEmail().trim());
        tecnico.setEspecialidade(requestDTO.getEspecialidade());
        tecnico.setAtivo(requestDTO.getAtivo() != null ? requestDTO.getAtivo() : Ativo.ATIVO);
        return converterParaResponse(tecnicoRepository.save(tecnico));
    }

    public TecnicoResponseDTO atualizar(Long id, TecnicoRequestDTO requestDTO) {
        Tecnico tecnico = buscarTecnicoPorId(id);
        validarDados(requestDTO, id);
        tecnico.setNome(requestDTO.getNome().trim());
        tecnico.setEmail(requestDTO.getEmail().trim());
        tecnico.setEspecialidade(requestDTO.getEspecialidade());
        if (requestDTO.getAtivo() != null) tecnico.setAtivo(requestDTO.getAtivo());
        return converterParaResponse(tecnicoRepository.save(tecnico));
    }

    public void deletar(Long id) {
        Tecnico tecnico = buscarTecnicoPorId(id);
        if (chamadoRepository.existsByTecnicosId(id)) {
            throw new RegraDeNegocioException("Não é possível excluir técnicos vinculados a chamados.");
        }
        tecnicoRepository.delete(tecnico);
    }

    private void validarEmail(String email, Long id) {
        boolean emailExiste = id == null
                ? tecnicoRepository.existsByEmail(email)
                : tecnicoRepository.existsByEmailAndIdNot(email, id);

        if (emailExiste) {
            throw new RegraDeNegocioException(
                    "Já existe um técnico cadastrado com esse e-mail."
            );
        }
    }

    private void validarDados(TecnicoRequestDTO requestDTO, Long id) {
        if (requestDTO.getNome() == null || requestDTO.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("O nome do técnico é obrigatório.");
        }
        if (requestDTO.getEmail() == null || requestDTO.getEmail().trim().isEmpty()) {
            throw new RegraDeNegocioException("O e-mail do técnico é obrigatório.");
        }

        validarEmail(requestDTO.getEmail().trim(), id);
    }

    private Tecnico buscarTecnicoPorId(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Técnico com ID "+id+" não encontrado."
                ));
    }

    private TecnicoResponseDTO converterParaResponse(Tecnico tecnico) {
        return new TecnicoResponseDTO(
                tecnico.getId(),
                tecnico.getNome(),
                tecnico.getEmail(),
                tecnico.getEspecialidade(),
                tecnico.getAtivo()
        );
    }
}