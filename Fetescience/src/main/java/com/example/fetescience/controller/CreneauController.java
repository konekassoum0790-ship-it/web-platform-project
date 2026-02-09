package com.example.fetescience.controller;

import com.example.fetescience.model.Creneau;
import com.example.fetescience.model.Participant;
import com.example.fetescience.repository.CreneauRepository;
import com.example.fetescience.repository.ParticipantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CreneauController {

    private final CreneauRepository creneauRepository;
    private final ParticipantRepository participantRepository;

    public CreneauController(CreneauRepository creneauRepository,
                             ParticipantRepository participantRepository) {
        this.creneauRepository = creneauRepository;
        this.participantRepository = participantRepository;
    }

    // GET http://localhost:8081/creneaux
    @GetMapping("/creneaux")
    public List<Creneau> getAllCreneaux() {
        return creneauRepository.findAll();
    }

    // GET http://localhost:8081/creneaux/{id}
    @GetMapping("/creneaux/{id}")
    public ResponseEntity<Creneau> getCreneauById(@PathVariable Long id) {
        return creneauRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET http://localhost:8081/creneaux/disponibles?horaire=10
    @GetMapping("/creneaux/disponibles")
    public List<Creneau> getCreneauxDisponibles(@RequestParam int horaire) {
        return creneauRepository
                .findByHoraireDebutLessThanEqualAndStatutFalse(horaire);
    }

    // GET http://localhost:8081/participants/1/creneaux
    @GetMapping("/participants/{idParticipant}/creneaux")
    public ResponseEntity<List<Creneau>> getCreneauxPourParticipant(
            @PathVariable Long idParticipant) {

        return participantRepository.findById(idParticipant)
                .map(p -> ResponseEntity.ok(
                        creneauRepository
                        .findByInscriptions_ParticipantOrderByHoraireDebutAsc(p)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ NEW ENDPOINT: Called by JavaScript when you select an Atelier
    @GetMapping("/api/ateliers/{atelierId}/creneaux")
    @ResponseBody
    public ResponseEntity<List<Creneau>> getCreneauxByAtelier(@PathVariable Long atelierId) {
        // Uses the method we fixed earlier (sorted by time)
        List<Creneau> creneaux = creneauRepository.findByAtelierIdOrderByHoraireDebutAsc(atelierId);
        return ResponseEntity.ok(creneaux);
    }
    // ✅ VALIDATION D'ADRESSE (OpenStreetMap / Nominatim)
    @GetMapping("/api/adresse/valider")
    public ResponseEntity<Boolean> validerAdresse(@RequestParam String adresse) {

        try {
            String encoded = java.net.URLEncoder.encode(adresse, java.nio.charset.StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encoded;

            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            boolean valide = response != null && response.contains("\"lat\"");

            return ResponseEntity.ok(valide);

        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
