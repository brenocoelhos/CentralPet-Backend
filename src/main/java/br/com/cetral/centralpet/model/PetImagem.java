package br.com.cetral.centralpet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "pet_imagens")
@Getter
@Setter
public class PetImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Timestamp criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = new Timestamp(System.currentTimeMillis());
        }
    }
}

