package br.com.cetral.centralpet.repository;

import br.com.cetral.centralpet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findAllByUsuarioId(String usuarioId);

    Optional<Pet> findByIdAndUsuarioEmail(Long id, String usuarioEmail);

    boolean existsByUsuarioIdAndNomeIgnoreCaseAndDataDesaparecimentoAndEspecieIgnoreCase(
            String usuarioId,
            String nome,
            LocalDate dataDesaparecimento,
            String especie
    );

    List<Pet> findAllByNomeContainingIgnoreCase(String nome);
}
