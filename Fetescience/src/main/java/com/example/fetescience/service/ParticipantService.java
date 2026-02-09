package com.example.fetescience.service;

import com.example.fetescience.model.Inscription;
import com.example.fetescience.model.Participant;
import com.example.fetescience.model.Creneau;
import com.example.fetescience.model.StatutInscription;
import com.example.fetescience.repository.InscriptionRepository;
import com.example.fetescience.repository.ParticipantRepository;
import com.example.fetescience.repository.CreneauRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParticipantService {

    private final ParticipantRepository participantRepo;
    private final CreneauRepository creneauRepo;
    private final InscriptionRepository inscriptionRepo;
    private final PasswordEncoder passwordEncoder; // ✅ AJOUTÉ

    public ParticipantService(ParticipantRepository pr,
                              CreneauRepository cr,
                              InscriptionRepository ir,
                              PasswordEncoder passwordEncoder) { // ✅ AJOUTÉ
        this.participantRepo = pr;
        this.creneauRepo = cr;
        this.inscriptionRepo = ir;
        this.passwordEncoder = passwordEncoder; // ✅ AJOUTÉ
    }

    // --- CRUD BASICS ---

    public Participant create(Participant p) {
        // ✅ Hasher le mot de passe
        if (p.getPassword() != null && !p.getPassword().startsWith("$2a$")) {
            p.setPassword(passwordEncoder.encode(p.getPassword()));
        }

        // ✅ Activer le compte par défaut
        p.setAccountVerified(true);

        return participantRepo.save(p);
    }

    public List<Participant> listAll() {
        return participantRepo.findAll();
    }

    public Participant getById(Long id) {
        return participantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + id));
    }

    public Optional<Participant> findById(Long id) {
        return participantRepo.findById(id);
    }

    /**
     * Recherche un participant par son nom.
     */
    public Optional<Participant> findByNom(String nom) {
        return participantRepo.findByNom(nom);
    }

    // --- BUSINESS LOGIC (INSCRIPTIONS) ---

    public void inscrire(Long participantId, Long creneauId) {
        Participant p = getById(participantId);
        Creneau c = creneauRepo.findById(creneauId)
                .orElseThrow(() -> new RuntimeException("Creneau not found"));

        // 1. Check if already registered
        boolean alreadyRegistered = p.getInscriptions().stream()
                .anyMatch(i -> i.getCreneau().equals(c));

        if (alreadyRegistered) {
            throw new RuntimeException("Le participant est déjà inscrit à ce créneau !");
        }

        // 2. Check Capacity
        if (c.isComplet()) {
            throw new RuntimeException("Désolé, ce créneau est complet !");
        }

        // 3. Create the Inscription
        Inscription inscription = new Inscription(p, c);
        inscription.setStatut(StatutInscription.VALIDEE);

        // 4. Save (Cascades will handle the lists, but saving explicitly is safer)
        inscriptionRepo.save(inscription);

        // 5. Update Lists (Keep Java objects in sync for this transaction)
        p.getInscriptions().add(inscription);
        c.getInscriptions().add(inscription);

        // 6. Update Creneau Status if full
        if (c.getInscriptions().size() >= c.getCapacite()) {
            c.setStatut(true);
            creneauRepo.save(c);
        }

        participantRepo.save(p);
    }

    public void desinscrire(Long participantId, Long creneauId) {
        Participant p = getById(participantId);
        Creneau c = creneauRepo.findById(creneauId)
                .orElseThrow(() -> new RuntimeException("Creneau not found"));

        // 1. Find the specific Inscription
        Inscription toDelete = p.getInscriptions().stream()
                .filter(i -> i.getCreneau().equals(c))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inscription introuvable pour ce créneau."));

        // 2. Remove from Java Lists (Memory Sync)
        p.getInscriptions().remove(toDelete);
        c.getInscriptions().remove(toDelete);

        // 3. Delete from Database
        inscriptionRepo.delete(toDelete);

        // 4. Update Creneau Status (It frees up a spot)
        if (c.isStatut()) {
            c.setStatut(false);
            creneauRepo.save(c);
        }

        participantRepo.save(p);
    }
}