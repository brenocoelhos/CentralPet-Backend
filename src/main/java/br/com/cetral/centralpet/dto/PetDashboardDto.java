package br.com.cetral.centralpet.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PetDashboardDto(
        Long id,
        String nome,
        String especie,
        String raca,
        String cor,
        String porte,
        LocalDate dataDesaparecimento,
        LocalDateTime dataCadastro, // Adicionado para o frontend ler a hora da publicação
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
