package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.model.PetImagem;
import br.com.cetral.centralpet.repository.PetImagemRepository;
import br.com.cetral.centralpet.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PetImagemService {

    private final PetImagemRepository petImagemRepository;
    private final PetRepository petRepository;
    private final S3Service s3Service;

    public PetImagemService(PetImagemRepository petImagemRepository,
                            PetRepository petRepository,
                            S3Service s3Service) {
        this.petImagemRepository = petImagemRepository;
        this.petRepository = petRepository;
        this.s3Service = s3Service;
    }

    /**
     * Faz upload de uma imagem para o S3 (pets/{petId}/uuid.jpg)
     * e persiste o registro no banco.
     */
    @Transactional
    public PetImagem uploadImagem(Long petId, MultipartFile file) throws IOException {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NoSuchElementException("Pet não encontrado: " + petId));

        S3Service.UploadResult result = s3Service.uploadImagemPet(petId, file);

        PetImagem imagem = new PetImagem();
        imagem.setPet(pet);
        imagem.setUrl(result.url());
        imagem.setS3Key(result.s3Key());

        return petImagemRepository.save(imagem);
    }

    /** Retorna todos os registros de imagem de um pet. */
    @Transactional(readOnly = true)
    public List<PetImagem> listar(Long petId) {
        return petImagemRepository.findAllByPetIdOrderByCriadoEmAsc(petId);
    }

    /** Retorna apenas as URLs das imagens de um pet. */
    public List<String> listarUrls(Long petId) {
        return listar(petId).stream().map(PetImagem::getUrl).toList();
    }

    /** Remove uma imagem do S3 e do banco. */
    @Transactional
    public void deletarImagem(Long petId, Long imagemId) {
        PetImagem imagem = petImagemRepository.findByIdAndPetId(imagemId, petId)
                .orElseThrow(() -> new NoSuchElementException("Imagem não encontrada"));
        s3Service.deletarImagem(imagem.getS3Key());
        petImagemRepository.delete(imagem);
    }
}
