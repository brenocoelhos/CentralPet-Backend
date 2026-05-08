package br.com.cetral.centralpet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Armazena imagens no disco local, organizadas em {uploadDir}/pets/{petId}/.
 * A URL pública fica em {baseUrl}/uploads/pets/{petId}/{filename}.
 */
@Service
public class LocalStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:4000}")
    private String baseUrl;

    public record UploadResult(String filePath, String url) {}

    /**
     * Salva o arquivo em disco e retorna o path relativo + URL pública.
     */
    public UploadResult salvarImagemPet(Long petId, MultipartFile file) throws IOException {
        String extensao = obterExtensao(file.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID() + extensao;

        // Cria a pasta pets/{petId}/ se não existir
        Path pasta = Paths.get(uploadDir, "pets", String.valueOf(petId));
        Files.createDirectories(pasta);

        Path destino = pasta.resolve(nomeArquivo);
        file.transferTo(destino.toFile());

        // Caminho relativo para salvar no banco (ex: pets/42/uuid.jpg)
        String caminhoRelativo = "pets/" + petId + "/" + nomeArquivo;

        // URL pública acessível pelo front
        String urlPublica = baseUrl + "/uploads/" + caminhoRelativo;

        return new UploadResult(caminhoRelativo, urlPublica);
    }

    /**
     * Remove o arquivo do disco.
     */
    public void deletarImagem(String caminhoRelativo) {
        try {
            Path arquivo = Paths.get(uploadDir, caminhoRelativo);
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            // Log e segue — não bloqueia a operação
            System.err.println("Aviso: não foi possível deletar arquivo " + caminhoRelativo + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) {
            return ".jpg";
        }
        return nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
    }
}

