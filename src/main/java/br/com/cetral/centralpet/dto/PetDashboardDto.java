package br.com.cetral.centralpet.dto;

import java.time.LocalDate;
import java.util.List;

public record PetDashboardDto(
        Long id,
        String nome,
        String especie,
        String raca,
        String cor,
        String porte,
        LocalDate dataDesaparecimento,
        String localDesaparecimento,
        List<String> descricao,
        String fotoUrl,
        String nomeTutor,
        String telefoneTutor,
        String usuarioId
) {
}

