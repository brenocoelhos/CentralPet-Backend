package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.LoginResponseDto;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GoogleAuthService {

    @Value("${google.client-id:${GOOGLE_CLIENT_ID:${GOOGLE_OAUTH_CLIENT_ID:}}}")
    private String googleClientId;

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public GoogleAuthService(UsuarioRepository usuarioRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Verifica o idToken com a API do Google, cria o usuário se não existir
     * e retorna um JWT próprio do CentralPet.
     */
    public LoginResponseDto loginComGoogle(String idToken) {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new IllegalArgumentException("Configuração ausente: google.client-id / GOOGLE_CLIENT_ID");
        }

        JsonNode payload = verificarTokenGoogle(idToken);

        String aud = getText(payload, "aud");
        if (!googleClientId.equals(aud)) {
            throw new IllegalArgumentException("Token Google inválido: client_id não corresponde");
        }

        String googleSub = getText(payload, "sub");
        String email = getText(payload, "email");
        String nome = payload.hasNonNull("name") && !payload.get("name").asText().isBlank()
                ? payload.get("name").asText()
                : (payload.hasNonNull("given_name") ? payload.get("given_name").asText() : email);

        // Tenta retornar usuário existente ou cria novo
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> criarUsuarioGoogle(googleSub, email, nome));

        // Valida que o usuário existe e tem ID antes de gerar JWT
        if (usuario == null || usuario.getId() == null || usuario.getId().isBlank()) {
            throw new IllegalArgumentException("Falha ao criar ou recuperar usuário do Google");
        }

        String token = jwtService.gerarToken(usuario);
        return new LoginResponseDto(
                token,
                "Bearer",
                jwtService.getJwtExpirationMs(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    private Usuario criarUsuarioGoogle(String googleSub, String email, String nome) {
        // Validações básicas
        if (googleSub == null || googleSub.isBlank()) {
            throw new IllegalArgumentException("Google sub (ID) vazio ou inválido");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email do Google vazio ou inválido");
        }
        if (nome == null || nome.isBlank()) {
            nome = email.split("@")[0]; // fallback: usar parte do email antes de @
        }

        // Normalizações
        nome = nome.trim();
        if (nome.length() > 150) {
            nome = nome.substring(0, 150);
        }

        // Cria ID: google_{sub} com limite de 128 chars
        String usuarioId = "google_" + googleSub;
        if (usuarioId.length() > 128) {
            // Se ultrapassa 128, usa hash curto do sub
            usuarioId = "google_" + Integer.toHexString(googleSub.hashCode()).substring(0, 120);
        }

        Usuario novo = new Usuario();
        novo.setId(usuarioId);
        novo.setEmail(email);
        novo.setNome(nome);
        novo.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));

        // Preenche campos legados obrigatórios
        preencherCamposObrigatoriosLegados(novo);

        try {
            Usuario salvo = usuarioRepository.save(novo);
            if (salvo == null || salvo.getId() == null) {
                throw new IllegalArgumentException("Usuário não foi persistido corretamente");
            }
            return salvo;
        } catch (DataIntegrityViolationException ex) {
            // Tenta recuperar se houver constraint violation (ex: email duplicado)
            return usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Não foi possível criar usuário: " + ex.getMessage()
                    ));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Erro ao persistir usuário Google: " + ex.getMessage(), ex);
        }
    }

    private void preencherCamposObrigatoriosLegados(Usuario usuario) {
        if (usuario.getDataNascimento() == null) {
            usuario.setDataNascimento(LocalDate.of(2000, 1, 1));
        }
        // CPF e telefone: gerar números aleatórios únicos
        if (usuario.getCpf() == null || usuario.getCpf().isBlank()) {
            usuario.setCpf(gerarCPFValido());
        }
        if (usuario.getTelefone() == null || usuario.getTelefone().isBlank()) {
            usuario.setTelefone(gerarTelefoneValido());
        }
    }

    private String gerarCPFValido() {
        // CPF com 11 dígitos aleatórios (válido tecnicamente, mas não confere dígitos verificadores)
        StringBuilder sb = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    private String gerarTelefoneValido() {
        // Telefone com 11 dígitos (DDD + número)
        // Formata como (XX) 9XXXX-XXXX depois remove formatação
        StringBuilder sb = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                sb.append(ThreadLocalRandom.current().nextInt(1, 10)); // DDD começa 1-9
            } else {
                sb.append(ThreadLocalRandom.current().nextInt(10));
            }
        }
        return sb.toString();
    }

    private String getText(JsonNode payload, String key) {
        JsonNode value = payload.get(key);
        if (value == null || value.isNull() || value.asText().isBlank()) {
            throw new IllegalArgumentException("Token Google inválido: campo ausente " + key);
        }
        return value.asText();
    }

    private JsonNode verificarTokenGoogle(String idToken) {
        try {
            String encodedToken = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + encodedToken))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());

            if (response.statusCode() != 200) {
                String googleError = body.has("error_description")
                        ? body.get("error_description").asText()
                        : (body.has("error") ? body.get("error").asText() : "Token Google inválido ou expirado");
                throw new IllegalArgumentException(googleError);
            }

            return body;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição de validação Google interrompida", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Não foi possível validar o token com o Google", e);
        }
    }
}

