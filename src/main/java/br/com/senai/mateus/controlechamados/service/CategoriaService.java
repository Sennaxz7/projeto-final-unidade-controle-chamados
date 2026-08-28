package br.com.senai.mateus.controlechamados.service;

import br.com.senai.mateus.controlechamados.dto.CategoriaRequestDTO;
import br.com.senai.mateus.controlechamados.dto.CategoriaResponseDTO;
import br.com.senai.mateus.controlechamados.entity.Categoria;
import br.com.senai.mateus.controlechamados.exception.ConflitoException;
import br.com.senai.mateus.controlechamados.exception.RecursoNaoEncontradoException;
import br.com.senai.mateus.controlechamados.exception.RegraDeNegocioException;
import br.com.senai.mateus.controlechamados.repository.CategoriaRepository;
import br.com.senai.mateus.controlechamados.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ChamadoRepository chamadoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ChamadoRepository chamadoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.chamadoRepository = chamadoRepository;
    }
    public List<CategoriaResponseDTO> buscar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public CategoriaResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarCategoriaPorId(id));
    }

    public CategoriaResponseDTO salvar(CategoriaRequestDTO requestDTO) {
        validarCategoria(requestDTO);
        if (categoriaRepository.existsByNomeIgnoreCase(requestDTO.getNome().trim())) {
            throw new RegraDeNegocioException("Ja existe categoria com esse nome.");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(requestDTO.getNome().trim());
        categoria.setDescricao(requestDTO.getDescricao().trim());

        return converterParaResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO requestDTO) {
        validarCategoria(requestDTO);
        Categoria categoriaAtualizada = buscarCategoriaPorId(id);
        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(requestDTO.getNome().trim(), id)) {
            throw new RegraDeNegocioException("Ja existe categoria com esse nome.");
        }

        categoriaAtualizada.setNome(requestDTO.getNome().trim());
        categoriaAtualizada.setDescricao(requestDTO.getDescricao().trim());

        return converterParaResponse(categoriaRepository.save(categoriaAtualizada));
    }

    public void excluir(Long id) {
        Categoria categoria = buscarCategoriaPorId(id);
        if(chamadoRepository.existsByCategoriaId(id)) {
            throw new ConflitoException(
                    "Não é possível excluir um categoria vinculada a um chamado."
            );
        }
        categoriaRepository.delete(categoria);
    }

    public Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria com ID "+id+" não encontrada."
                ));
    }

    private void validarCategoria(CategoriaRequestDTO requestDTO) {
        if (requestDTO.getNome() == null || requestDTO.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException(
                    "O nome da Categoria é obrigatório"
            );
        }
        if (requestDTO.getDescricao() == null || requestDTO.getDescricao().trim().isEmpty()) {
            throw new RegraDeNegocioException(
                    "A descrição da Categoria é obrigatória"
            );
        }
    }
    public CategoriaResponseDTO converterParaResponse(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
