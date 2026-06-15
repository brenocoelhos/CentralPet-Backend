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
        Instant dataCadastro,
        String localDesaparecimento,
        List<String> descricao,
        Boolean castrado,
        Boolean vacinado,
        Boolean recompensa,
        String fotoUrl,
        List<String> imagens,
        String nomeTutor,
        String telefoneTutor,
        String usuarioId,
        String cep,
        Double latitude,
        Double longitude,
        String fotoTutor // 
) {
    public PetDashboardDto(
            Long id, String nome, String especie, String raca, String cor, String porte,
            LocalDate dataDesaparecimento, Instant dataCadastro, String localDesaparecimento,
            List<String> descricao, Boolean castrado, Boolean vacinado, Boolean recompensa,
            String fotoUrl, List<String> imagens, String nomeTutor, String telefoneTutor,
            String usuarioId, String cep, Double latitude, Double longitude
    ) {
        this(id, nome, especie, raca, cor, porte, dataDesaparecimento, dataCadastro,
             localDesaparecimento, descricao, castrado, vacinado, recompensa, fotoUrl,
             imagens, nomeTutor, telefoneTutor, usuarioId, cep, latitude, longitude, null);
    }
}
