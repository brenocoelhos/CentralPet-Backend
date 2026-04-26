package br.com.cetral.centralpet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CadastroPetDto {

    @NotBlank(message = "ID do usuário é obrigatório")
    @Size(max = 128, message = "ID do usuário deve ter no máximo 128 caracteres")
    private String usuarioId;

    @NotBlank(message = "Nome do pet é obrigatório")
    @Size(max = 100, message = "Nome do pet deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Espécie é obrigatória")
    @Size(max = 50, message = "Espécie deve ter no máximo 50 caracteres")
    private String especie;

    @Size(max = 100, message = "Raça deve ter no máximo 100 caracteres")
    private String raca;

    @Size(max = 80, message = "Cor deve ter no máximo 80 caracteres")
    private String cor;

    @Size(max = 20, message = "Porte deve ter no máximo 20 caracteres")
    private String porte;

    @NotNull(message = "Data do desaparecimento é obrigatória")
    private LocalDate dataDesaparecimento;

    @NotBlank(message = "Local do desaparecimento é obrigatório")
    @Size(max = 300, message = "Local do desaparecimento deve ter no máximo 300 caracteres")
    private String localDesaparecimento;

    @Size(max = 20, message = "Descrição pode conter no máximo 20 chips")
    private List<@NotBlank(message = "Cada chip da descrição deve ser preenchido")
            @Size(max = 80, message = "Cada chip da descrição deve ter no máximo 80 caracteres") String> descricao;

    @Size(max = 500, message = "URL da foto deve ter no máximo 500 caracteres")
    private String fotoUrl;

    @NotBlank(message = "Nome do tutor é obrigatório")
    @Size(max = 150, message = "Nome do tutor deve ter no máximo 150 caracteres")
    private String nomeTutor;

    @NotBlank(message = "Telefone do tutor é obrigatório")
    @Pattern(regexp = "^\\d{10,15}$", message = "Telefone do tutor deve conter entre 10 e 15 dígitos")
    private String telefoneTutor;
}
