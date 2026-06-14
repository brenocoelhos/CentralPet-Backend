package br.com.cetral.centralpet.dto;

public class UsuarioLogadoDto {

    private final String id;
    private final String nome;
    private final String email;
    private final String fotoPerfil;

    // Construtor antigo (mantido para não quebrar o LoginService)
    public UsuarioLogadoDto(String id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.fotoPerfil = null;
    }

    // Novo construtor completo
    public UsuarioLogadoDto(String id, String nome, String email, String fotoPerfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.fotoPerfil = fotoPerfil;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getFotoPerfil() { return fotoPerfil; }
}
