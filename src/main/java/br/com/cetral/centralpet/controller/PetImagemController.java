package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.model.PetImagem;
import br.com.cetral.centralpet.service.PetImagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/pets/{petId}/imagens")
public class PetImagemController {

    private final PetImagemService petImagemService;

    public PetImagemController(PetImagemService petImagemService) {
        this.petImagemService = petImagemService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImagem(
            @PathVariable Long petId,
            @RequestParam("file") MultipartFile file) {

        try {
            PetImagem imagem = petImagemService.uploadImagem(petId, file);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", imagem.getId(),
                            "url", imagem.getUrl()
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "erro", e.getMessage()
                    ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro interno ao enviar imagem"
                    ));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarImagens(
            @PathVariable Long petId) {

        List<Map<String, Object>> response = petImagemService.listar(petId)
                .stream()
                .map(img -> Map.<String, Object>of(
                        "id", img.getId(),
                        "url", img.getUrl()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imagemId}")
    public ResponseEntity<Map<String, String>> deletarImagem(
            @PathVariable Long petId,
            @PathVariable Long imagemId) {

        petImagemService.deletarImagem(petId, imagemId);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Imagem removida com sucesso"
        ));
    }
}
