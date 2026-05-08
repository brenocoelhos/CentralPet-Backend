package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.model.PetImagem;
import br.com.cetral.centralpet.service.PetImagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/pets/{petId}/imagens")
public class PetImagemController {

    private final PetImagemService petImagemService;

    public PetImagemController(PetImagemService petImagemService) {
        this.petImagemService = petImagemService;
    }

    /** Upload de uma imagem — GlobalExceptionHandler trata erros */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadImagem(
            @PathVariable Long petId,
            @RequestParam("file") MultipartFile file) throws IOException {
        PetImagem imagem = petImagemService.uploadImagem(petId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", imagem.getId(), "url", imagem.getUrl()));
    }

    /** Lista todas as imagens de um pet (id + url) */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarImagens(@PathVariable Long petId) {
        List<Map<String, Object>> response = petImagemService.listar(petId)
                .stream()
                .map(img -> Map.<String, Object>of("id", img.getId(), "url", img.getUrl()))
                .toList();
        return ResponseEntity.ok(response);
    }

    /** Remove uma imagem — GlobalExceptionHandler trata NoSuchElementException como 404 */
    @DeleteMapping("/{imagemId}")
    public ResponseEntity<String> deletarImagem(
            @PathVariable Long petId,
            @PathVariable Long imagemId) {
        petImagemService.deletarImagem(petId, imagemId);
        return ResponseEntity.ok("Imagem removida com sucesso");
    }
}
