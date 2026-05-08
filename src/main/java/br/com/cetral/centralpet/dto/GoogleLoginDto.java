package br.com.cetral.centralpet.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleLoginDto {

    @NotBlank(message = "idToken é obrigatório")
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
