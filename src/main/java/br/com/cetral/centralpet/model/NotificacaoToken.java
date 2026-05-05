package br.com.cetral.centralpet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@SuppressWarnings("JpaDataSourceORMInspection")
@Table(name = "notificacoes_token")
@Getter
@Setter
public class NotificacaoToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    //noinspection JpaDataSourceORMInspection
    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    //noinspection JpaDataSourceORMInspection
    @Column(name = "atualizado_em", nullable = false)
    private Timestamp atualizadoEm;

    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    void atualizarTimestamp() {
        atualizadoEm = new Timestamp(System.currentTimeMillis());
    }
}

