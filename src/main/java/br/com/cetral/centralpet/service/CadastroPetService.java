package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.CadastroPetDto;
import br.com.cetral.centralpet.dto.PetDashboardDto;
import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.PetRepository;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CadastroPetService {

    private static final String CHIP_DELIMITER = "::";
    private static final String LEGACY_CHIP_DELIMITER = "||";

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;

    public CadastroPetService(PetRepository petRepository, UsuarioRepository usuarioRepository) {
        this.petRepository = petRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Pet cadastrar(CadastroPetDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        boolean anuncioDuplicado = petRepository.existsByUsuarioIdAndNomeIgnoreCaseAndDataDesaparecimentoAndEspecieIgnoreCase(
                dto.getUsuarioId(),
                dto.getNome().trim(),
                dto.getDataDesaparecimento(),
                dto.getEspecie().trim()
        );

        if (anuncioDuplicado) {
            throw new IllegalArgumentException("Você já possui um anúncio desse pet para esta data de desaparecimento");
        }

        Pet pet = new Pet();
        pet.setUsuario(usuario);
        pet.setNome(dto.getNome().trim());
        pet.setEspecie(dto.getEspecie().trim());
        pet.setRaca(normalizarOpcional(dto.getRaca()));
        pet.setCor(normalizarOpcional(dto.getCor()));
        pet.setPorte(normalizarOpcional(dto.getPorte()));
        pet.setDataDesaparecimento(dto.getDataDesaparecimento());
        pet.setLocalDesaparecimento(dto.getLocalDesaparecimento().trim());
        pet.setDescricao(serializarDescricao(dto.getDescricao()));
        pet.setCastrado(dto.getCastrado());
        pet.setVacinado(dto.getVacinado());
        pet.setRecompensa(dto.getRecompensa());
        pet.setFotoUrl(normalizarOpcional(dto.getFotoUrl()));
        pet.setNomeTutor(dto.getNomeTutor().trim());
        pet.setTelefoneTutor(dto.getTelefoneTutor().trim());

        return petRepository.save(pet);
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String valorNormalizado = valor.trim();
        return valorNormalizado.isEmpty() ? null : valorNormalizado;
    }

    private String serializarDescricao(List<String> chipsDescricao) {
        List<String> chipsNormalizados = normalizarChipsDescricao(chipsDescricao);
        if (chipsNormalizados.isEmpty()) {
            return null;
        }

        return String.join(CHIP_DELIMITER, chipsNormalizados);
    }

    private List<String> deserializarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return List.of();
        }

        String texto = descricao.trim();
        String delimitador = texto.contains(CHIP_DELIMITER) ? CHIP_DELIMITER : LEGACY_CHIP_DELIMITER;
        return normalizarChipsDescricao(List.of(texto.split(java.util.regex.Pattern.quote(delimitador))));
    }

    private List<String> normalizarChipsDescricao(List<String> chipsDescricao) {
        if (chipsDescricao == null || chipsDescricao.isEmpty()) {
            return List.of();
        }

        return chipsDescricao.stream()
                .filter(chip -> chip != null && !chip.isBlank())
                .map(String::trim)
                .toList();
    }

    public List<PetDashboardDto> buscarTodos(String nome, String especie, String cor, String porte, String usuarioId) {
        List<Pet> petsBase = usuarioId == null || usuarioId.isBlank()
                ? petRepository.findAll()
                : petRepository.findAllByUsuarioId(usuarioId.trim());

        return petsBase.stream()
                .filter(pet -> contemIgnorandoCase(pet.getNome(), nome))
                .filter(pet -> contemIgnorandoCase(pet.getEspecie(), especie))
                .filter(pet -> contemIgnorandoCase(pet.getCor(), cor))
                .filter(pet -> contemIgnorandoCase(pet.getPorte(), porte))
                .map(this::toDashboardDto)
                .toList();
    }

    public void deletarCadastro(Long petId, String emailUsuarioLogado) {
        if (!petRepository.existsById(petId)) {
            throw new NoSuchElementException("Pet não encontrado");
        }

        Pet pet = petRepository.findByIdAndUsuarioEmail(petId, emailUsuarioLogado)
                .orElseThrow(() -> new SecurityException("Você não tem permissão para excluir este cadastro"));

        petRepository.delete(pet);
    }

    private boolean contemIgnorandoCase(String valor, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        return valor != null && valor.toLowerCase().contains(filtro.trim().toLowerCase());
    }

    private PetDashboardDto toDashboardDto(Pet pet) {
        return new PetDashboardDto(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getCor(),
                pet.getPorte(),
                pet.getDataDesaparecimento(),
                pet.getLocalDesaparecimento(),
                deserializarDescricao(pet.getDescricao()),
                pet.getCastrado(),
                pet.getVacinado(),
                pet.getRecompensa(),
                pet.getFotoUrl(),
                pet.getNomeTutor(),
                pet.getTelefoneTutor(),
                pet.getUsuario() != null ? pet.getUsuario().getId() : null
        );
    }
}
