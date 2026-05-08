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

    /**
     * Upload genérico (sem vínculo a pet).
     */
    public String uploadImagem(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();
        return upload(key, file);
    }

    /**
     * Upload organizado por pet: pasta pets/{petId}/{uuid}-{filename}.
     * Retorna o S3 key gerado.
     */
    public UploadResult uploadImagemPet(Long petId, MultipartFile file) throws IOException {
        String key = "pets/" + petId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        String url = upload(key, file);
        return new UploadResult(key, url);
    }

    /**
     * Remove um objeto do S3 pela sua key.
     */
    public void deletarImagem(String s3Key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build());
    }

    // ---------------------------------------------------------------

    private String upload(String key, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    // ---------------------------------------------------------------

    public record UploadResult(String s3Key, String url) {}
}
