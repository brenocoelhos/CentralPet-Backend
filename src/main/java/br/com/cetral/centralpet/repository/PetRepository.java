package br.com.cetral.centralpet.repository;

import br.com.cetral.centralpet.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%'))) AND " +
            "(:especie IS NULL OR LOWER(p.especie) LIKE LOWER(CONCAT('%', CAST(:especie AS string), '%'))) AND " +
            "(:cor IS NULL OR LOWER(p.cor) LIKE LOWER(CONCAT('%', CAST(:cor AS string), '%'))) AND " +
            "(:porte IS NULL OR LOWER(p.porte) LIKE LOWER(CONCAT('%', CAST(:porte AS string), '%'))) AND " +
            "(:usuarioId IS NULL OR p.usuario.id = :usuarioId)")
    Page<Pet> buscarComFiltros(@Param("nome") String nome,
                               @Param("especie") String especie,
                               @Param("cor") String cor,
                               @Param("porte") String porte,
                               @Param("usuarioId") String usuarioId,
                               Pageable pageable);
}
