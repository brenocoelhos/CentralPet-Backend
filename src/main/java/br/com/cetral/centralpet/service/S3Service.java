package br.com.cetral.centralpet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadImagem(MultipartFile file) throws IOException {
        String key = "uploads/" + gerarNomeSeguro(file);
        return upload(key, file);
    }

    public UploadResult uploadImagemPet(Long petId, MultipartFile file) throws IOException {
        validarArquivo(file);

        String key = "pets/" + petId + "/" + gerarNomeSeguro(file);
        String url = upload(key, file);

        return new UploadResult(key, url);
    }

    public void deletarImagem(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return;
        }

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build());
    }

    private String upload(String key, MultipartFile file) throws IOException {
        String contentType = obterContentType(file);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    private void validarArquivo(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("Arquivo não enviado");
        }

        if (file.isEmpty() || file.getSize() <= 0) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
    }

    private String gerarNomeSeguro(MultipartFile file) {
        String nomeOriginal = file.getOriginalFilename();

        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            nomeOriginal = "foto.jpg";
        }

        nomeOriginal = nomeOriginal
                .replace("\\", "/");

        nomeOriginal = nomeOriginal.substring(nomeOriginal.lastIndexOf("/") + 1);

        nomeOriginal = nomeOriginal
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        if (!nomeOriginal.contains(".")) {
            nomeOriginal = nomeOriginal + ".jpg";
        }

        return UUID.randomUUID() + "-" + nomeOriginal;
    }

    private String obterContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            return "image/jpeg";
        }

        return contentType;
    }

    public record UploadResult(String s3Key, String url) {}
}
