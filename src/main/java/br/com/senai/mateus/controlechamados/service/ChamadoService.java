package br.com.senai.mateus.controlechamados.service;

import br.com.senai.mateus.controlechamados.dto.*;
import br.com.senai.mateus.controlechamados.entity.Categoria;
import br.com.senai.mateus.controlechamados.entity.Chamado;
import br.com.senai.mateus.controlechamados.entity.Tecnico;
import br.com.senai.mateus.controlechamados.enums.Ativo;
import br.com.senai.mateus.controlechamados.enums.StatusChamado;
import br.com.senai.mateus.controlechamados.exception.ConflitoException;
import br.com.senai.mateus.controlechamados.exception.RecursoNaoEncontradoException;
import br.com.senai.mateus.controlechamados.exception.RegraDeNegocioException;
import br.com.senai.mateus.controlechamados.repository.ChamadoRepository;
import br.com.senai.mateus.controlechamados.repository.TecnicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChamadoService {
    private final ChamadoRepository chamadoRepository;
    private final CategoriaService categoriaService;
    private final TecnicoRepository tecnicoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository, CategoriaService categoriaService, TecnicoRepository tecnicoRepository) {
        this.chamadoRepository = chamadoRepository;
        this.categoriaService = categoriaService;
        this.tecnicoRepository = tecnicoRepository;
    }

    public List<ChamadoResponseDTO> buscar() {
        return chamadoRepository.findAll()
                .stream()
                .map(this::converterParaResponse).toList();
    }

    public ChamadoResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarChamadoPorId(id));
    }

    public ChamadoResponseDTO salvar(ChamadoRequestDTO chamadoRequestDTO) {
        validarDados(chamadoRequestDTO);
        Chamado chamado = new Chamado();
        Categoria categoria = categoriaService.buscasCategoriaPorId(chamadoRequestDTO.getCategoriaId());
        List<Tecnico> tecnicos = buscarTecnicos(chamadoRequestDTO.getTecnicosIds());
        validarTecnicosAtivo(tecnicos);

        chamado.setTitulo(chamadoRequestDTO.getTitulo().trim());
        chamado.setDescricao(chamadoRequestDTO.getDescricao().trim());
        chamado.setLocal(chamadoRequestDTO.getLocal().trim());
        chamado.setSolicitante(chamadoRequestDTO.getSolicitante().trim());
        chamado.setPrioridade(chamadoRequestDTO.getPrioridade());
        chamado.setCategoria(categoria);
        chamado.setTecnicos(tecnicos);
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setDataAbertura(LocalDate.now());

        return converterParaResponse(chamadoRepository.save(chamado));
    }

    public ChamadoResponseDTO atualizar(Long id, ChamadoRequestDTO chamadoRequestDTO) {
        Chamado chamado = buscarChamadoPorId(id);
        validarDados(chamadoRequestDTO);
        if (chamado.getStatus() == StatusChamado.FINALIZADO){
            throw new RegraDeNegocioException("Não é possível alterar um chamado finalizado.");
        }
        Categoria categoria = categoriaService.buscasCategoriaPorId(chamadoRequestDTO.getCategoriaId());
        validarIdsUnicos(chamadoRequestDTO.getTecnicosIds());
        List<Tecnico> tecnicos = buscarTecnicos(chamadoRequestDTO.getTecnicosIds());
        validarTecnicosAtivo(tecnicos);
        chamado.setTitulo(chamadoRequestDTO.getTitulo().trim());
        chamado.setDescricao(chamadoRequestDTO.getDescricao().trim());
        chamado.setSolicitante(chamadoRequestDTO.getSolicitante().trim());
        chamado.setLocal(chamadoRequestDTO.getLocal().trim());
        chamado.setPrioridade(chamadoRequestDTO.getPrioridade());
        chamado.setCategoria(categoria);
        chamado.setTecnicos(tecnicos);

        return converterParaResponse(chamadoRepository.save(chamado));
    }

    public void deletar(Long id) {
        Chamado chamado = buscarChamadoPorId(id);
        if (chamado.getStatus() == StatusChamado.FINALIZADO) {
            throw new RegraDeNegocioException("Não é possível excluir um chamado Finalizado.");
        }
        chamadoRepository.delete(chamado);
    }

    public ChamadoResponseDTO atualizarStatus(Long id, AtualizarStatusDTO statusDTO) {
        Chamado chamado = buscarChamadoPorId(id);
        if (statusDTO == null || statusDTO.getStatusChamado() == null) {
            throw new RegraDeNegocioException(
                    "O status do chamado é obrigatório."
            );
        }
        if (chamado.getStatus() == StatusChamado.FINALIZADO) {
            throw new RegraDeNegocioException("Não é possível alterar o status de um chamado finalizado.");
        }
        if (statusDTO.getStatusChamado() == StatusChamado.EM_ANDAMENTO && (chamado.getTecnicos() == null
        || chamado.getTecnicos().isEmpty())) {
            throw new RegraDeNegocioException(
                    "Um chamado só poderá ser alterado para EM_ANDAMENTO se possuir pelo menos um técnico vinculado.");
        }
        chamado.setStatus(statusDTO.getStatusChamado());
        if (statusDTO.getStatusChamado() == StatusChamado.FINALIZADO) {
            chamado.setDataFinalizacao(LocalDate.now());
        } else {
            chamado.setDataFinalizacao(null);
        }
        return converterParaResponse(chamadoRepository.save(chamado));
    }

    public ChamadoResponseDTO vincularTecnicos(Long id, AlterarTecnicoDTO tecnicoDTO) {
        Chamado chamado = buscarChamadoPorId(id);
        if (chamado.getStatus() == StatusChamado.FINALIZADO) {
            throw new RegraDeNegocioException("Um chamado FINALIZADO não poderá receber novos técnicos.");
        }
        List<Long> ids = tecnicoDTO.getTecnicosIds();
        validarIdsUnicos(ids);
        if (ids == null || ids.isEmpty()) {
            throw new RegraDeNegocioException(
                    "Informe pelo menos um técnico."
            );
        }

        List<Tecnico> tecnicos = buscarTecnicos(ids);
        validarTecnicosAtivo(tecnicos);
        validarTecnicosJaVinculados(chamado, tecnicos);
        chamado.getTecnicos().addAll(tecnicos);
        return converterParaResponse(chamadoRepository.save(chamado));
    }

    public ChamadoResponseDTO desvincularTecnicos(Long id, AlterarTecnicoDTO tecnicoDTO) {
        Chamado chamado = buscarChamadoPorId(id);
        if (chamado.getStatus() == StatusChamado.FINALIZADO) {
            throw new RegraDeNegocioException("Não é possível desvincular técnicos de um chamado FINALIZADO.");
        }
        List<Long> tecnicoIds = tecnicoDTO.getTecnicosIds();
        validarIdsUnicos(tecnicoIds);
        validarTecnicosVinculados(chamado, tecnicoIds);

        chamado.getTecnicos().removeIf(tecnico ->
                tecnicoIds.contains(tecnico.getId()));
        return converterParaResponse(chamadoRepository.save(chamado));
    }

    private Chamado buscarChamadoPorId(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado com o ID "+id+" não foi encontrado."
                ));
    }

    private void validarTecnicosVinculados(Chamado chamado, List<Long> tecnicosIds) {
        if (tecnicosIds == null || tecnicosIds.isEmpty()) {
            throw new RegraDeNegocioException(
                    "Informe pelo menos um técnico para desvincular."
            );
        }

        if (chamado.getTecnicos() == null || chamado.getTecnicos().isEmpty()) {
            throw new RegraDeNegocioException(
                    "Este chamado não possui técnicos vinculados."
            );
        }

        for (Long tecnicoId : tecnicosIds) {
            boolean estaVinculado = chamado.getTecnicos().stream()
                    .anyMatch( tecnico -> tecnico.getId().equals(tecnicoId));
            if (!estaVinculado) {
                throw new RegraDeNegocioException(
                        "O técnico com ID "+tecnicoId+" não está vinculado a este chamado."
                );
            }
        }
    }

    private void validarTecnicosJaVinculados(Chamado chamado, List<Tecnico> novosTecnicos) {
        List<Tecnico> tecnicosAtuais = chamado.getTecnicos();
        if (tecnicosAtuais == null || tecnicosAtuais.isEmpty()) return;
        for (Tecnico t : novosTecnicos) {
            boolean jaVinculado = tecnicosAtuais.stream()
                    .anyMatch(existem -> existem.getId().equals(t.getId()));
            if (jaVinculado) {
                throw new ConflitoException(
                        "O técnico " + t.getNome() + " já está vinculado a este chamado."
                );
            }
        }
    }

    private List<Tecnico> buscarTecnicos(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<Tecnico> tecnicos = tecnicoRepository.findAllById(ids);
        if (tecnicos.size() != ids.size()) {
            throw new RegraDeNegocioException("Um ou mais técnicos não foram encontrados.");
        }
        return tecnicos;
    }

    private void validarTecnicosAtivo(List<Tecnico> tecnico) {
        for (Tecnico t : tecnico) {
            if (t.getAtivo() == Ativo.INATIVO) {
                throw new RegraDeNegocioException("O técnico " + t.getNome() + " está inativo e não pode ser vinculado.");
            }
        }
    }

    private void validarIdsUnicos(List<Long> ids) {
        if (ids != null && ids.stream().distinct().count() != ids.size()) {
            throw new RegraDeNegocioException(
                    "Não é permitido informar técnicos duplicados."
            );
        }
    }

    private void validarDados(ChamadoRequestDTO chamadoRequestDTO) {
        if (chamadoRequestDTO.getTitulo() == null || chamadoRequestDTO.getTitulo().trim().isEmpty()) {
            throw new RegraDeNegocioException(
                    "O título do chamado é obrigatório."
            );
        }
        if (chamadoRequestDTO.getCategoriaId() == null) {
            throw new RegraDeNegocioException(
                    "A categoria do chamado é obrigatório."
            );
        }
        if (chamadoRequestDTO.getDescricao() == null || chamadoRequestDTO.getDescricao().trim().isEmpty()) {
            throw new RegraDeNegocioException(
                    "A descrição do chamado é obrigatório."
            );
        }
        if (chamadoRequestDTO.getLocal() == null || chamadoRequestDTO.getLocal().trim().isEmpty()) {
            throw new RegraDeNegocioException(
                    "O local do chamado é obrigatório."
            );
        }
        if (chamadoRequestDTO.getSolicitante() == null || chamadoRequestDTO.getSolicitante().trim().isEmpty()) {
            throw new RegraDeNegocioException(
                    "O solicitante do chamado é obrigatório."
            );
        }
        if (chamadoRequestDTO.getPrioridade() == null) {
            throw new RegraDeNegocioException(
                    "A prioridade do chamado é obrigatório."
            );
        }
    }

    private ChamadoResponseDTO converterParaResponse(Chamado chamado) {
        CategoriaResponseDTO categoriaResponseDTO = categoriaService.converterParaResponse(chamado.getCategoria());
        List<TecnicoResponseDTO> tecnicoResponseDTOList = chamado.getTecnicos()
                .stream()
                .map(tecnico -> new TecnicoResponseDTO(
                        tecnico.getId(),
                        tecnico.getNome(),
                        tecnico.getEmail(),
                        tecnico.getEspecialidade(),
                        tecnico.getAtivo()
                )).toList();
        return new ChamadoResponseDTO(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getSolicitante(),
                chamado.getLocal(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                chamado.getDataAbertura(),
                chamado.getDataFinalizacao(),
                categoriaResponseDTO,
                tecnicoResponseDTOList
        );
    }
}
