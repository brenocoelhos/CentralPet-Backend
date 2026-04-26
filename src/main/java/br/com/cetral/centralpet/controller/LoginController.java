package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.dto.CadastroDto;
import br.com.cetral.centralpet.dto.LoginDto;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.service.LoginService;
import br.com.cetral.centralpet.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;
    private final UsuarioService usuarioService;

    public LoginController(LoginService loginService, UsuarioService usuarioService) {
        this.loginService = loginService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto) {
        String resultado = loginService.login(loginDto.getEmail(), loginDto.getSenha());
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastro(@Valid @RequestBody CadastroDto cadastroDto) {
        Usuario usuario = usuarioService.cadastrar(cadastroDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário cadastrado com sucesso: " + usuario.getNome());
    }
}
