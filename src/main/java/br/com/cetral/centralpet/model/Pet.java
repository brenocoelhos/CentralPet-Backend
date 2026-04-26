package br.com.cetral.centralpet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
@Getter
@Setter
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 100)
    private String raca;

    @Column(length = 80)
    private String cor;

    @Column(length = 20)
    private String porte;

    @Column(name = "data_desaparecimento")
    private LocalDate dataDesaparecimento;

    @Column(name = "local_desaparecimento", length = 300)
    private String localDesaparecimento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "nome_tutor", nullable = false, length = 150)
    private String nomeTutor;

    @Column(name = "telefone_tutor", nullable = false, length = 15)
    private String telefoneTutor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Timestamp criadoEm;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetTag> tags = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = new Timestamp(System.currentTimeMillis());
        }
    }
}

