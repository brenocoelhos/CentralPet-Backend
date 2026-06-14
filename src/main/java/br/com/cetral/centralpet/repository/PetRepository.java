package br.com.cetral.centralpet.repository;

import br.com.cetral.centralpet.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByIdAndUsuarioEmail(Long id, String usuarioEmail);

    boolean existsByUsuarioIdAndNomeIgnoreCaseAndDataDesaparecimentoAndEspecieIgnoreCase(
            String usuarioId,
            String nome,
            LocalDate dataDesaparecimento,
            String especie
    );

    /**
     * Busca paginada com filtros opcionais aplicados no banco.
     * Parâmetros nulos ignoram o filtro correspondente.
     * Resolve o problema de: filtros em memória + sem paginação.
     */
    @Query("SELECT p FROM Pet p WHERE " +
            "(:termo IS NULL OR " +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', CAST(:termo AS string), '%')) OR " +
            "LOWER(p.raca) LIKE LOWER(CONCAT('%', CAST(:termo AS string), '%')) OR " +
            "LOWER(p.localDesaparecimento) LIKE LOWER(CONCAT('%', CAST(:termo AS string), '%')) OR " +
            "LOWER(p.cep) LIKE LOWER(CONCAT('%', CAST(:termo AS string), '%'))) AND " +
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) AND " +
            "(:especie IS NULL OR LOWER(p.especie) LIKE LOWER(CONCAT('%', CAST(:especie AS string), '%'))) AND " +
            "(:usarGrupoEspecie = false OR " +
            "(:grupoOutros = false AND LOWER(p.especie) IN :especiesGrupo) OR " +
            "(:grupoOutros = true AND LOWER(p.especie) NOT IN :especiesGrupo)) AND " +
            "(:raca IS NULL OR LOWER(p.raca) LIKE LOWER(CONCAT('%', CAST(:raca AS string), '%'))) AND " +
            "(:cor IS NULL OR LOWER(p.cor) LIKE LOWER(CONCAT('%', CAST(:cor AS string), '%'))) AND " +
            "(:porte IS NULL OR LOWER(p.porte) LIKE LOWER(CONCAT('%', CAST(:porte AS string), '%'))) AND " +
            "(:bairro IS NULL OR LOWER(p.localDesaparecimento) LIKE LOWER(CONCAT('%', CAST(:bairro AS string), '%'))) AND " +
            "(:usuarioId IS NULL OR p.usuario.id = :usuarioId)")
    Page<Pet> buscarComFiltros(@Param("termo") String termo,
                               @Param("nome") String nome,
                               @Param("especie") String especie,
                               @Param("usarGrupoEspecie") boolean usarGrupoEspecie,
                               @Param("grupoOutros") boolean grupoOutros,
                               @Param("especiesGrupo") List<String> especiesGrupo,
                               @Param("raca") String raca,
                               @Param("cor") String cor,
                               @Param("porte") String porte,
                               @Param("bairro") String bairro,
                               @Param("usuarioId") String usuarioId,
                               Pageable pageable);
}
