package br.com.senai.mateus.controlechamados.service;

import br.com.senai.mateus.controlechamados.dto.CategoriaRequestDTO;
import br.com.senai.mateus.controlechamados.dto.CategoriaResponseDTO;
import br.com.senai.mateus.controlechamados.entity.Categoria;
import br.com.senai.mateus.controlechamados.exception.RecursoNaoEncontradoException;
import br.com.senai.mateus.controlechamados.exception.RegraDeNegocioException;
import br.com.senai.mateus.controlechamados.repository.CategoriaRepository;
import br.com.senai.mateus.controlechamados.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    public final CategoriaRepository categoriaRepository;
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
        return converterParaResponse(buscasCategoriaPorId(id));
    }

    public CategoriaResponseDTO salvar(CategoriaRequestDTO requestDTO) {
        validarCategoria(requestDTO);
        Categoria categoria = new Categoria();
        categoria.setNome(requestDTO.getNome());
        categoria.setDescricao(requestDTO.getDescricao());

        return converterParaResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO requestDTO) {
        validarCategoria(requestDTO);
        Categoria categoriaAtualizada = buscasCategoriaPorId(id);
        categoriaAtualizada.setNome(requestDTO.getNome());
        categoriaAtualizada.setDescricao(requestDTO.getDescricao());

        return converterParaResponse(categoriaRepository.save(categoriaAtualizada));
    }

    public void excluir(Long id) {
        Categoria categoria = buscasCategoriaPorId(id);
        if(chamadoRepository.existsByCategoriaId(id)) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir um categoria vinculada a um chamado."
            );
        }
        categoriaRepository.delete(categoria);
    }

    public Categoria buscasCategoriaPorId(Long id) {
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
    }
    public CategoriaResponseDTO converterParaResponse(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
