package br.com.senai.mateus.controlechamados.controller;

import br.com.senai.mateus.controlechamados.dto.TecnicoRequestDTO;
import br.com.senai.mateus.controlechamados.dto.TecnicoResponseDTO;
import br.com.senai.mateus.controlechamados.service.TecnicoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {
    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    public List<TecnicoResponseDTO> buscar() {
        return tecnicoService.buscar();
    }

    @GetMapping("/{id}")
    public TecnicoResponseDTO buscarPorId(@PathVariable Long id) {
        return tecnicoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TecnicoResponseDTO salvar(@RequestBody TecnicoRequestDTO dto) {
        return tecnicoService.salvar(dto);
    }

    @PutMapping("/{id}")
    public TecnicoResponseDTO atualizar(@PathVariable Long id, @RequestBody TecnicoRequestDTO dto) {
        return tecnicoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        tecnicoService.deletar(id);
    }

}
