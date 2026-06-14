package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.dto.CadastroPetDto;
import br.com.cetral.centralpet.dto.PageResponseDto;
import br.com.cetral.centralpet.dto.PetDashboardDto;
import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.service.CadastroPetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PetController {

    private final CadastroPetService cadastroPetService;

    public PetController(CadastroPetService cadastroPetService) {
        this.cadastroPetService = cadastroPetService;
    }

    @PostMapping("/cadastro-pet")
    public ResponseEntity<Map<String, Object>> cadastroPet(@Valid @RequestBody CadastroPetDto cadastroPetDto) {
        Pet pet = cadastroPetService.cadastrar(cadastroPetDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", pet.getId(),
                        "nome", pet.getNome(),
                        "message", "Pet cadastrado com sucesso"
                ));
    }

    /**
     * Busca paginada de pets com filtros opcionais.
     * Resposta: { content: [...], page, size, totalElements, totalPages, hasNext }
     */
    @GetMapping("/busca-pets")
    public ResponseEntity<PageResponseDto<PetDashboardDto>> buscaPets(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) String porte,
            @RequestParam(required = false) String usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponseDto<PetDashboardDto> resultado =
                cadastroPetService.buscarTodos(nome, especie, cor, porte, usuarioId, page, size);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/cadastro-pet/{petId}")
    public ResponseEntity<PetDashboardDto> buscarCadastroPet(@PathVariable Long petId) {
        return ResponseEntity.ok(cadastroPetService.buscarPorId(petId));
    }

    @PutMapping("/cadastro-pet/{petId}")
    public ResponseEntity<PetDashboardDto> atualizarCadastroPet(
            @PathVariable Long petId,
            @Valid @RequestBody CadastroPetDto cadastroPetDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cadastroPetService.atualizarCadastro(petId, cadastroPetDto, authentication.getName())
        );
    }

    @DeleteMapping("/cadastro-pet/{petId}")
    public ResponseEntity<String> deletarCadastroPet(@PathVariable Long petId, Authentication authentication) {
        cadastroPetService.deletarCadastro(petId, authentication.getName());
        return ResponseEntity.ok("Cadastro do pet removido com sucesso");
    }
}

