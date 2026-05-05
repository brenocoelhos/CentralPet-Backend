package br.com.cetral.centralpet.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacaoTokenDto {

    @NotBlank(message = "Token de notificação é obrigatório")
    @Size(max = 255, message = "Token de notificação deve ter no máximo 255 caracteres")
    private String token;

    @NotBlank(message = "ID do usuário é obrigatório")
    @Size(max = 128, message = "ID do usuário deve ter no máximo 128 caracteres")
    private String userId;

    @DecimalMin(value = "-90.0", message = "Latitude deve ser maior ou igual a -90")
    @DecimalMax(value = "90.0", message = "Latitude deve ser menor ou igual a 90")
    private Double lat;

    @DecimalMin(value = "-180.0", message = "Longitude deve ser maior ou igual a -180")
    @DecimalMax(value = "180.0", message = "Longitude deve ser menor ou igual a 180")
    private Double lng;
}

