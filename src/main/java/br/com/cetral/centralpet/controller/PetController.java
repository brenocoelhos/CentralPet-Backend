package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.dto.CadastroPetDto;
import br.com.cetral.centralpet.dto.PetDashboardDto;
import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.service.CadastroPetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class PetController {

    private final CadastroPetService cadastroPetService;

    public PetController(CadastroPetService cadastroPetService) {
        this.cadastroPetService = cadastroPetService;
    }

    @PostMapping("/cadastro-pet")
    public ResponseEntity<String> cadastroPet(@Valid @RequestBody CadastroPetDto cadastroPetDto) {
        Pet pet = cadastroPetService.cadastrar(cadastroPetDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Pet cadastrado com sucesso: " + pet.getNome());
    }

    @GetMapping("/busca-pets")
    public ResponseEntity<List<PetDashboardDto>> buscaPets(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) String porte,
            @RequestParam(required = false) String usuarioId
    ) {
        List<PetDashboardDto> pets = cadastroPetService.buscarTodos(nome, especie, cor, porte, usuarioId);
        return ResponseEntity.ok(pets);
    }
}

