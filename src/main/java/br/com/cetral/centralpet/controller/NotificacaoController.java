package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.dto.NotificacaoTokenDto;
import br.com.cetral.centralpet.service.NotificacaoTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoTokenService notificacaoTokenService;

    public NotificacaoController(NotificacaoTokenService notificacaoTokenService) {
        this.notificacaoTokenService = notificacaoTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> registrarToken(@Valid @RequestBody NotificacaoTokenDto dto) {
        notificacaoTokenService.salvarOuAtualizar(dto);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Token registrado com sucesso"
        ));
    }
}

