package br.com.cetral.centralpet.dto;

public class LoginResponseDto {

    private final String token;
    private final String tipo;
    private final long expiraEmMs;
    private final String nome;
    private final String email;

    public LoginResponseDto(String token, String tipo, long expiraEmMs, String nome, String email) {
        this.token = token;
        this.tipo = tipo;
        this.expiraEmMs = expiraEmMs;
        this.nome = nome;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public long getExpiraEmMs() {
        return expiraEmMs;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}

