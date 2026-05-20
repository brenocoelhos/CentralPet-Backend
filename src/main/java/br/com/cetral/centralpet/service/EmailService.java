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

    public EmailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    public void enviarEmailRecuperacaoSenha(String destino, String nome, String link) {
        String html =
                "<p>Olá, " + nome + "!</p>" +
                "<p>Recebemos uma solicitação para redefinir sua senha no CentralPet.</p>" +
                "<p>Clique no botão abaixo para criar uma nova senha:</p>" +
                "<p>" +
                "<a href=\"" + link + "\" " +
                "style=\"display:inline-block;background:#D97757;color:#ffffff;padding:12px 20px;" +
                "border-radius:999px;text-decoration:none;font-weight:bold;\">" +
                "Redefinir senha" +
                "</a>" +
                "</p>" +
                "<p>Esse link é válido por 30 minutos.</p>" +
                "<p>Se você não solicitou essa recuperação, ignore este email.</p>" +
                "<p>CentralPet</p>";

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
                "htmlContent", html
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
