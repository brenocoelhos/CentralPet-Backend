package br.com.cetral.centralpet.repository;

import br.com.cetral.centralpet.model.PetImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetImagemRepository extends JpaRepository<PetImagem, Long> {

    List<PetImagem> findAllByPetIdOrderByCriadoEmAsc(Long petId);

    /** Carrega imagens de múltiplos pets em uma única query — evita N+1 */
    List<PetImagem> findAllByPetIdInOrderByCriadoEmAsc(List<Long> petIds);

    Optional<PetImagem> findByIdAndPetId(Long id, Long petId);

    void deleteAllByPetId(Long petId);
}



