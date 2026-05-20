package br.com.cetral.centralpet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public EmailService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    public void enviarEmailRecuperacaoSenha(String destino, String nome, String link) {
        String texto =
                "Olá, " + nome + "!\n\n" +
                "Recebemos uma solicitação para redefinir sua senha no CentralPet.\n\n" +
                "Clique no link abaixo para criar uma nova senha:\n" +
                link + "\n\n" +
                "Esse link é válido por 30 minutos.\n\n" +
                "Se você não solicitou essa recuperação, ignore este email.\n\n" +
                "CentralPet";

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of(
                                "email", destino,
                                "name", nome
                        )
                ),
                "subject", "Recuperação de senha - CentralPet",
                "textContent", texto
        );

        restClient.post()
                .uri("/smtp/email")
                .header("api-key", brevoApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
