package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.service.S3Service;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/auth/usuarios")
public class PerfilUploadController {

    private final S3Service s3Service;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public PerfilUploadController(S3Service s3Service, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.s3Service = s3Service;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/upload-perfil", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @RequestParam("file") MultipartFile file) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido"));
            }
            
            //
            String urlDaFoto = s3Service.uploadImagem(file);

            // 2. Descobre quem é o usuário pelo token e já salva no banco aqui mesmo
            String token = authHeader.substring(7);
            String email = jwtService.extrairEmail(token);
            
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            
            usuario.setFotoPerfil(urlDaFoto);
            usuarioRepository.save(usuario);

            // 3. Retorna a URL igual o pet fazia
            return ResponseEntity.ok(Map.of("fotoPerfil", urlDaFoto));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao enviar imagem", "detalhe", e.toString()));
        }
    }
}
