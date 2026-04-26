package br.com.cetral.centralpet.dto;

import java.time.LocalDate;

public record PetDashboardDto(
        Long id,
        String nome,
        String especie,
        String raca,
        String cor,
        String porte,
        LocalDate dataDesaparecimento,
        String localDesaparecimento,
        String descricao,
        String fotoUrl,
        String nomeTutor,
        String telefoneTutor,
        String usuarioId
) {
}

