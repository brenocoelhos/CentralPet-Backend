package br.com.cetral.centralpet.service;

import br.com.cetral.centralpet.model.NotificacaoToken;
import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.repository.NotificacaoTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class PushNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationService.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final NotificacaoTokenRepository notificacaoTokenRepository;
    private final RestClient restClient;
    private final double raioKm;

    public PushNotificationService(NotificacaoTokenRepository notificacaoTokenRepository,
                                   @Value("${app.notifications.expo-url}") String expoUrl,
                                   @Value("${app.notifications.radius-km}") double raioKm) {
        this.notificacaoTokenRepository = notificacaoTokenRepository;
        this.restClient = RestClient.create(expoUrl);
        this.raioKm = raioKm;
    }

    public void notificarPetPerdido(Pet pet, Double latDesaparecimento, Double lngDesaparecimento) {
        if (latDesaparecimento == null || lngDesaparecimento == null) {
            return;
        }

        String tutorId = pet.getUsuario() != null ? pet.getUsuario().getId() : "";
        List<NotificacaoToken> tokensNoRaio = notificacaoTokenRepository.findAllByUserIdNot(tutorId).stream()
                .filter(token -> distanciaKm(latDesaparecimento, lngDesaparecimento, token.getLat(), token.getLng()) <= raioKm)
                .toList();

        if (tokensNoRaio.isEmpty()) {
            return;
        }

        List<Map<String, Object>> mensagens = tokensNoRaio.stream()
                .map(token -> Map.of(
                        "to", token.getToken(),
                        "title", "Pet desaparecido próximo a você",
                        "body", "Um novo pet foi cadastrado como perdido na sua região",
                        "data", Map.of("petId", String.valueOf(pet.getId()))
                ))
                .toList();

        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mensagens)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException ex) {
            LOGGER.warn("Falha ao enviar notificações push do pet {}: {}", pet.getId(), ex.getMessage());
        }
    }

    private double distanciaKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}


