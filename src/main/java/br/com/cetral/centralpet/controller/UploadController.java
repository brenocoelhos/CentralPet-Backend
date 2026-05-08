package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @deprecated Substituído por {@link PetImagemController}.
 * Mantido temporariamente para não quebrar clientes antigos. Remover na próxima major.
 */
@Deprecated
@RestController
@RequestMapping("/upload")
public class UploadController {

    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file
    ) {
        try {

            String url = s3Service.uploadImagem(file);

            return ResponseEntity.ok(url);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar imagem");
        }
    }
}