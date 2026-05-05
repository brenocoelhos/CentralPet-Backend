package br.com.cetral.centralpet.repository;

import br.com.cetral.centralpet.model.NotificacaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacaoTokenRepository extends JpaRepository<NotificacaoToken, Long> {

    Optional<NotificacaoToken> findByToken(String token);

    List<NotificacaoToken> findAllByUserIdNot(String userId);
}

