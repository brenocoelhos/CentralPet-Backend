package br.com.cetral.centralpet.dto;

public class UsuarioLogadoDto {

    private final String id;
    private final String nome;
    private final String email;

    public UsuarioLogadoDto(String id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}

