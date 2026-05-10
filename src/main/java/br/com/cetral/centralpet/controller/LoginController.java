package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.dto.CadastroDto;
import br.com.cetral.centralpet.dto.EsqueciSenhaDto;
import br.com.cetral.centralpet.dto.GoogleLoginDto;
import br.com.cetral.centralpet.dto.LoginDto;
import br.com.cetral.centralpet.dto.LoginResponseDto;
import br.com.cetral.centralpet.dto.RedefinirSenhaDto;
import br.com.cetral.centralpet.dto.UsuarioLogadoDto;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.service.GoogleAuthService;
import br.com.cetral.centralpet.service.JwtService;
import br.com.cetral.centralpet.service.LoginService;
import br.com.cetral.centralpet.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;

    public LoginController(LoginService loginService, UsuarioService usuarioService, JwtService jwtService, GoogleAuthService googleAuthService) {
        this.loginService = loginService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        LoginResponseDto resultado = loginService.login(loginDto.getEmail(), loginDto.getSenha());
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDto> loginGoogle(@Valid @RequestBody GoogleLoginDto googleLoginDto) {
        LoginResponseDto resultado = googleAuthService.loginComGoogle(googleLoginDto.getIdToken());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioLogadoDto> me(Authentication authentication) {
        UsuarioLogadoDto usuario = loginService.buscarUsuarioLogado(authentication.getName());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastro(@Valid @RequestBody CadastroDto cadastroDto) {
        Usuario usuario = usuarioService.cadastrar(cadastroDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário cadastrado com sucesso: " + usuario.getNome());
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@Valid @RequestBody EsqueciSenhaDto dto) {
        loginService.solicitarRecuperacaoSenha(dto.getEmail());
        return ResponseEntity.ok("Se o email estiver cadastrado, enviaremos um link de recuperação.");
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@Valid @RequestBody RedefinirSenhaDto dto) {
        loginService.redefinirSenha(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token não fornecido");
        }

        String token = authHeader.substring(7);
        jwtService.invalidarToken(token);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}