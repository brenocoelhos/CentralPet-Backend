package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.LoginResponseDto;
import br.com.cetral.centralpet.dto.UsuarioLogadoDto;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

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

    public UsuarioLogadoDto buscarUsuarioLogado(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return new UsuarioLogadoDto(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
