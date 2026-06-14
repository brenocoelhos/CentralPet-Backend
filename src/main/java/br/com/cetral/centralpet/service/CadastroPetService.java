package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.dto.CadastroPetDto;
import br.com.cetral.centralpet.dto.PageResponseDto;
import br.com.cetral.centralpet.dto.PetDashboardDto;
import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.model.PetImagem;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.PetImagemRepository;
import br.com.cetral.centralpet.repository.PetRepository;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CadastroPetService {

    private static final String CHIP_DELIMITER = "::";
    private static final String LEGACY_CHIP_DELIMITER = "||";

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;
    private final PushNotificationService pushNotificationService;
    private final PetImagemRepository petImagemRepository;

    public CadastroPetService(PetRepository petRepository,
                              UsuarioRepository usuarioRepository,
                              PushNotificationService pushNotificationService,
                              PetImagemRepository petImagemRepository) {
        this.petRepository = petRepository;
        this.usuarioRepository = usuarioRepository;
        this.pushNotificationService = pushNotificationService;
        this.petImagemRepository = petImagemRepository;
    }

    @Transactional
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

        Pet petSalvo = petRepository.save(pet);

        pushNotificationService.notificarPetPerdido(
                petSalvo,
                dto.getLatitudeDesaparecimento(),
                dto.getLongitudeDesaparecimento()
        );

        return petSalvo;
    }

    @Transactional(readOnly = true)
    public PageResponseDto<PetDashboardDto> buscarTodos(
            String nome, String especie, String cor, String porte, String usuarioId,
            int page, int size) {

        // Converte strings vazias para null — JPQL usa IS NULL para ignorar filtro
        String nomeFilter     = isBlank(nome)      ? null : nome.trim();
        String especieFilter  = isBlank(especie)   ? null : especie.trim();
        String corFilter      = isBlank(cor)        ? null : cor.trim();
        String porteFilter    = isBlank(porte)      ? null : porte.trim();
        String usuarioFilter  = isBlank(usuarioId) ? null : usuarioId.trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());

        // Filtros aplicados no banco — sem carregar tudo na memória
        Page<Pet> petsPage = petRepository.buscarComFiltros(
                nomeFilter, especieFilter, corFilter, porteFilter, usuarioFilter, pageable);

        // Batch load de imagens — 1 query para todos os pets da página (evita N+1)
        List<Long> petIds = petsPage.getContent().stream().map(Pet::getId).toList();
        Map<Long, List<String>> imagensPorPet = Collections.emptyMap();
        if (!petIds.isEmpty()) {
            imagensPorPet = petImagemRepository.findAllByPetIdInOrderByCriadoEmAsc(petIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                            img -> img.getPet().getId(),
                            Collectors.mapping(PetImagem::getUrl, Collectors.toList())
                    ));
        }

        final Map<Long, List<String>> imagensMap = imagensPorPet;
        Page<PetDashboardDto> dtoPage = petsPage.map(pet ->
                toDashboardDto(pet, imagensMap.getOrDefault(pet.getId(), List.of())));

        return new PageResponseDto<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.hasNext()
        );
    }

    @Transactional
    public void deletarCadastro(Long petId, String emailUsuarioLogado) {
        if (!petRepository.existsById(petId)) {
            throw new NoSuchElementException("Pet não encontrado");
        }

        Pet pet = petRepository.findByIdAndUsuarioEmail(petId, emailUsuarioLogado)
                .orElseThrow(() -> new SecurityException("Você não tem permissão para excluir este cadastro"));

        petRepository.delete(pet);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) return null;
        String v = valor.trim();
        return v.isEmpty() ? null : v;
    }

    private String serializarDescricao(List<String> chipsDescricao) {
        List<String> chips = normalizarChipsDescricao(chipsDescricao);
        return chips.isEmpty() ? null : String.join(CHIP_DELIMITER, chips);
    }

    private List<String> deserializarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) return List.of();
        String texto = descricao.trim();
        String delimitador = texto.contains(CHIP_DELIMITER) ? CHIP_DELIMITER : LEGACY_CHIP_DELIMITER;
        return normalizarChipsDescricao(List.of(texto.split(java.util.regex.Pattern.quote(delimitador))));
    }

    private List<String> normalizarChipsDescricao(List<String> chips) {
        if (chips == null || chips.isEmpty()) return List.of();
        return chips.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(String::trim)
                .toList();
    }

    private PetDashboardDto toDashboardDto(Pet pet, List<String> imagens) {
        return new PetDashboardDto(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getCor(),
                pet.getPorte(),
                pet.getDataDesaparecimento(),
                pet.getCriadoEm() != null ? pet.getCriadoEm().toInstant() : null, // MÁGICA DO FUSO AQUI (toInstant)
                pet.getLocalDesaparecimento(),
                deserializarDescricao(pet.getDescricao()),
                pet.getCastrado(),
                pet.getVacinado(),
                pet.getRecompensa(),
                pet.getFotoUrl(),
                imagens,
                pet.getNomeTutor(),
                pet.getTelefoneTutor(),
                pet.getUsuario() != null ? pet.getUsuario().getId() : null
        );
    }
}
