package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.LoginResponseDto;
import br.com.cetral.centralpet.dto.UsuarioLogadoDto;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    public LoginService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta");
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

    @Transactional(readOnly = true)
    public UsuarioLogadoDto buscarUsuarioLogado(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return new UsuarioLogadoDto(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            Timestamp expiracao = Timestamp.from(Instant.now().plusSeconds(30 * 60));

            usuario.setTokenRecuperacao(token);
            usuario.setTokenExpiracao(expiracao);
            usuarioRepository.save(usuario);

            String link = resetPasswordUrl + "?token=" + token;
            emailService.enviarEmailRecuperacaoSenha(usuario.getEmail(), usuario.getNome(), link);
        });
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacao(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado"));

        if (usuario.getTokenExpiracao() == null || usuario.getTokenExpiracao().before(new Timestamp(System.currentTimeMillis()))) {
            usuario.setTokenRecuperacao(null);
            usuario.setTokenExpiracao(null);
            usuarioRepository.save(usuario);
            throw new IllegalArgumentException("Token inválido ou expirado");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTokenRecuperacao(null);
        usuario.setTokenExpiracao(null);
        usuarioRepository.save(usuario);
    }
}