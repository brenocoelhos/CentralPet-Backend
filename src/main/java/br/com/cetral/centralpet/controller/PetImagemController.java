package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.model.PetImagem;
import br.com.cetral.centralpet.service.PetImagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
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

            System.out.println("========== UPLOAD IMAGEM ==========");
            System.out.println("PET ID: " + petId);
            System.out.println("ORIGINAL FILENAME: " + file.getOriginalFilename());
            System.out.println("CONTENT TYPE: " + file.getContentType());
            System.out.println("SIZE: " + file.getSize());
            System.out.println("EMPTY: " + file.isEmpty());

            PetImagem imagem = petImagemService.uploadImagem(petId, file);

            System.out.println("UPLOAD OK");
            System.out.println("IMAGEM ID: " + imagem.getId());
            System.out.println("IMAGEM URL: " + imagem.getUrl());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", imagem.getId(),
                            "url", imagem.getUrl()
                    ));

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> erro = new LinkedHashMap<>();

            erro.put("erroClasse", e.getClass().getName());
            erro.put("erroMensagem",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Mensagem nula");

            erro.put("causa",
                    e.getCause() != null
                            ? e.getCause().toString()
                            : "Sem causa");

            erro.put("petId", petId);

            try {
                erro.put("fileOriginalName",
                        file != null ? file.getOriginalFilename() : null);

                erro.put("fileContentType",
                        file != null ? file.getContentType() : null);

                erro.put("fileSize",
                        file != null ? file.getSize() : null);

                erro.put("fileIsEmpty",
                        file != null && file.isEmpty());

            } catch (Exception ignored) {
            }

            System.out.println("========== ERRO UPLOAD ==========");
            System.out.println(erro);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(erro);
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
    public ResponseEntity<String> deletarImagem(
            @PathVariable Long petId,
            @PathVariable Long imagemId) {

        petImagemService.deletarImagem(petId, imagemId);

        return ResponseEntity.ok("Imagem removida com sucesso");
    }
}
