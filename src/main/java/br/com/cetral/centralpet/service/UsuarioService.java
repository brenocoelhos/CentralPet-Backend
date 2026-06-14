package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.CadastroDto;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service; // Adicionamos o serviço de upload

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, S3Service s3Service) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
    }

    @Transactional
    public Usuario cadastrar(CadastroDto dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já está cadastrado");
        }
        if (usuarioRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já está cadastrado");
        }
        if (usuarioRepository.existsByTelefone(dto.getTelefone())) {
            throw new IllegalArgumentException("Telefone já está cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID().toString());
        usuario.setNome(dto.getNome());
        usuario.setCpf(dto.getCpf());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTelefone(dto.getTelefone());
        usuario.setRua(dto.getRua());
        usuario.setNumero(dto.getNumero());
        usuario.setCidade(dto.getCidade());
        usuario.setEstado(dto.getEstado());

        return usuarioRepository.save(usuario);
    }

    // NOVO MÉTODO PARA ATUALIZAR A FOTO
    @Transactional
    public String atualizarFotoPerfil(String email, MultipartFile file) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        try {
            // Faz o upload usando o seu serviço S3 já existente
            String fotoUrl = s3Service.uploadImagem(file);
            
            // Atualiza o utilizador e guarda na base de dados
            usuario.setFotoPerfil(fotoUrl);
            usuarioRepository.save(usuario);
            
            return fotoUrl;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da foto de perfil", e);
        }
    }
}
