package br.com.cetral.centralpet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/**
 * Configuração do cliente S3 da AWS.
 * Se as credenciais explícitas (aws.accessKey / aws.secretKey) não estiverem
 * configuradas, usa o DefaultCredentialsProvider (instance-profile, env vars,
 * ~/.aws/credentials). Isso permite que a aplicação suba em dev sem S3.
 */
@Configuration
public class AwsS3Config {

    @Value("${aws.accessKey:}")
    private String accessKey;

    @Value("${aws.secretKey:}")
    private String secretKey;

    @Value("${aws.region:us-east-2}")
    private String region;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder().region(Region.of(region));

        if (accessKey != null && !accessKey.isBlank()) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        } else {
            // Sem credenciais explícitas: usa a cadeia padrão (instance profile, etc.)
            // O app sobe normalmente; chamadas S3 falharão em runtime se não houver creds.
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return builder.build();
    }
}

