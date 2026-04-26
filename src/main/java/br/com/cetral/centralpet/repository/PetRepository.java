package br.com.cetral.centralpet.repository;

import br.com.cetral.centralpet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findAllByUsuarioId(String usuarioId);

    boolean existsByUsuarioIdAndNomeIgnoreCaseAndDataDesaparecimentoAndEspecieIgnoreCase(
            String usuarioId,
            String nome,
            LocalDate dataDesaparecimento,
            String especie
    );

    List<Pet> findAllByNomeContainingIgnoreCase(String nome);
}
