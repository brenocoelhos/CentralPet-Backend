package br.com.cetral.centralpet.dto;

public class LoginResponseDto {

    private final String token;
    private final String tipo;
    private final long expiraEmMs;
    private final String nome;
    private final String email;
    private final String fotoPerfil;

    // Construtor antigo (mantido para não quebrar o código existente)
    public LoginResponseDto(String token, String tipo, long expiraEmMs, String nome, String email) {
        this.token = token;
        this.tipo = tipo;
        this.expiraEmMs = expiraEmMs;
        this.nome = nome;
        this.email = email;
        this.fotoPerfil = null;
    }

    // Novo construtor completo
    public LoginResponseDto(String token, String tipo, long expiraEmMs, String nome, String email, String fotoPerfil) {
        this.token = token;
        this.tipo = tipo;
        this.expiraEmMs = expiraEmMs;
        this.nome = nome;
        this.email = email;
        this.fotoPerfil = fotoPerfil;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public long getExpiraEmMs() { return expiraEmMs; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getFotoPerfil() { return fotoPerfil; }
}
