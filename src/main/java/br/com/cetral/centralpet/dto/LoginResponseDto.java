package br.com.cetral.centralpet.dto;

import java.time.LocalDate;

public record LoginResponseDto(
        boolean authenticated,
        String message,
        UsuarioLogadoDto user
) {
    public record UsuarioLogadoDto(
            String id,
            String nome,
            String email,
            String telefone,
            String endereco,
            LocalDate dataNascimento
    ) {
    }
}

