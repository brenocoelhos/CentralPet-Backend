package br.com.cetral.centralpet.dto;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

public record PetDashboardDto(
        Long id,
        String nome,
        String especie,
        String raca,
        String cor,
        String porte,
        LocalDate dataDesaparecimento,
        Instant dataCadastro, // Mudou de LocalDateTime para Instant
        String localDesaparecimento,
        List<String> descricao,
        Boolean castrado,
        Boolean vacinado,
        Boolean recompensa,
        String fotoUrl,
        List<String> imagens,
        String nomeTutor,
        String telefoneTutor,
        String usuarioId
) {
}
