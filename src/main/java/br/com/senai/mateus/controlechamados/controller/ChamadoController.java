package br.com.senai.mateus.controlechamados.controller;

import br.com.senai.mateus.controlechamados.dto.AlterarTecnicoDTO;
import br.com.senai.mateus.controlechamados.dto.AtualizarStatusDTO;
import br.com.senai.mateus.controlechamados.dto.ChamadoRequestDTO;
import br.com.senai.mateus.controlechamados.dto.ChamadoResponseDTO;
import br.com.senai.mateus.controlechamados.service.ChamadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {
    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public List<ChamadoResponseDTO> buscar(){
        return chamadoService.buscar();
    }

    @GetMapping("/{id}")
    public ChamadoResponseDTO buscarPorId(@PathVariable Long id) {
        return chamadoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChamadoResponseDTO salvar(@RequestBody ChamadoRequestDTO dto) {
        return chamadoService.salvar(dto);
    }

    @PutMapping("/{id}")
    public ChamadoResponseDTO atualizar(@PathVariable Long id, @RequestBody ChamadoRequestDTO dto) {
        return chamadoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        chamadoService.deletar(id);
    }

    @PatchMapping("/{id}/status")
    public ChamadoResponseDTO atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusDTO dto) {
        return chamadoService.atualizarStatus(id, dto);
    }

    @PatchMapping("/{id}/tecnicos")
    public ChamadoResponseDTO vincularTecnicos(@PathVariable Long id, @RequestBody AlterarTecnicoDTO dto) {
        return chamadoService.vincularTecnicos(id, dto);
    }

    @PatchMapping("/{id}/tecnicos/desvincular")
    public ChamadoResponseDTO desvincularTecnicos(@PathVariable Long id, @RequestBody AlterarTecnicoDTO dto) {
        return chamadoService.desvincularTecnicos(id, dto);
    }

}
