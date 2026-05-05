package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {

    @Value("${app.jwt.secret:centralpet-secret-key-do-breno-2026}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    private final Set<String> blacklist = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public String gerarToken(Usuario usuario) {
        Instant expiraEm = Instant.now().plusMillis(jwtExpirationMs);

        String jti = UUID.randomUUID().toString();
        String header = base64Encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Encode(String.format(
                "{\"sub\":\"%s\",\"jti\":\"%s\",\"iat\":%d,\"exp\":%d}",
                usuario.getEmail(), jti, Instant.now().getEpochSecond(), expiraEm.getEpochSecond()));

        String signature = assinar(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public String extrairEmail(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token ausente");
        }
        if (blacklist.contains(token)) {
            throw new IllegalArgumentException("Token foi invalidado (logout)");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Token inválido");
        }

        String assinaturaEsperada = assinar(parts[0] + "." + parts[1]);
        if (!assinaturaEsperada.equals(parts[2])) {
            throw new IllegalArgumentException("Assinatura inválida");
        }

        String payloadJson = decodificar(parts[1]);
        if (!payloadJson.contains("\"exp\":")) {
            throw new IllegalArgumentException("Payload inválido");
        }

        long expiration = extrairExp(payloadJson);
        if (Instant.now().getEpochSecond() > expiration) {
            throw new IllegalArgumentException("Token expirado");
        }

        return extrairSub(payloadJson);
    }

    public boolean tokenValido(String token, String email) {
        try {
            return email.equals(extrairEmail(token));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void invalidarToken(String token) {
        blacklist.add(token);
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    private String assinar(String conteudo) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec chave = new SecretKeySpec(
                    jwtSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmac.init(chave);
            byte[] assinatura = hmac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
            return base64Encode(assinatura);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao assinar token", ex);
        }
    }

    private String base64Encode(String texto) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String base64Encode(byte[] dados) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(dados);
    }

    private String decodificar(String encoded) {
        return new String(
                Base64.getUrlDecoder().decode(encoded + "=="),
                StandardCharsets.UTF_8
        );
    }

    private long extrairExp(String payloadJson) {
        int start = payloadJson.indexOf("\"exp\":") + 6;
        int end = payloadJson.indexOf(",", start);
        if (end == -1) {
            end = payloadJson.indexOf("}", start);
        }
        return Long.parseLong(payloadJson.substring(start, end).trim());
    }

    private String extrairSub(String payloadJson) {
        int start = payloadJson.indexOf("\"sub\":\"") + 7;
        int end = payloadJson.indexOf("\"", start);
        return payloadJson.substring(start, end);
    }
}







