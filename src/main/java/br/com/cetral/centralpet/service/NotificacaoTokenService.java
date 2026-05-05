package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.NotificacaoTokenDto;
import br.com.cetral.centralpet.model.NotificacaoToken;
import br.com.cetral.centralpet.repository.NotificacaoTokenRepository;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoTokenService {

    private final NotificacaoTokenRepository notificacaoTokenRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoTokenService(NotificacaoTokenRepository notificacaoTokenRepository,
                                   UsuarioRepository usuarioRepository) {
        this.notificacaoTokenRepository = notificacaoTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void salvarOuAtualizar(NotificacaoTokenDto dto) {
        String tokenNormalizado = dto.getToken().trim();
        String userIdNormalizado = dto.getUserId().trim();

        if (dto.getLat() == null || dto.getLng() == null) {
            throw new IllegalArgumentException("Latitude e longitude são obrigatórias para registrar notificações");
        }

        if (!usuarioRepository.existsById(userIdNormalizado)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        NotificacaoToken token = notificacaoTokenRepository.findByToken(tokenNormalizado)
                .orElseGet(NotificacaoToken::new);

        token.setToken(tokenNormalizado);
        token.setUserId(userIdNormalizado);
        token.setLat(dto.getLat());
        token.setLng(dto.getLng());

        notificacaoTokenRepository.save(token);
    }
}



