package com.example.fetescience.service;

import com.example.fetescience.model.*;
import com.example.fetescience.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final PersonneRepository personneRepository;
    private final AtelierRepository atelierRepository;
    private final CreneauRepository creneauRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository,
                              PersonneRepository personneRepository,
                              AtelierRepository atelierRepository,
                              CreneauRepository creneauRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.personneRepository = personneRepository;
        this.atelierRepository = atelierRepository;
        this.creneauRepository = creneauRepository;
    }

    // ========== MÉTHODES POUR L'ADMIN ==========

    /**
     * Récupère toutes les inscriptions (pour l'admin)
     */
    public List<Inscription> getAllInscriptions() {
        return inscriptionRepository.findAll();
    }

    /**
     * Accepter une inscription (admin)
     */
    @Transactional
    public void accepterInscription(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscription non trouvée"));

        inscription.setStatut(StatutInscription.VALIDEE);
        inscriptionRepository.save(inscription);
    }

    /**
     * Refuser une inscription (admin)
     */
    @Transactional
    public void refuserInscription(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscription non trouvée"));

        inscription.setStatut(StatutInscription.REFUSEE);
        inscriptionRepository.save(inscription);
    }

    // ========== MÉTHODES POUR LES PARTICIPANTS ==========

    /**
     * Récupère les inscriptions d'un participant par son email
     */
    public List<Inscription> getInscriptionsByEmail(String email) {
        Personne personne = personneRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        return inscriptionRepository.findByParticipant((Participant) personne);
    }

    /**
     * ✅ Récupère les inscriptions par ID participant (pour ancien contrôleur)
     */
    public List<Inscription> getInscriptionsByParticipant(Long participantId) {
        Personne personne = personneRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant non trouvé"));

        return inscriptionRepository.findByParticipant((Participant) personne);
    }

    /**
     * Inscrire un participant à un créneau (via email - pour Spring Security)
     */
    @Transactional
    public void inscrireParticipant(String email, Long creneauId) {
        Personne personne = personneRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (!(personne instanceof Participant)) {
            throw new IllegalArgumentException("Seuls les participants peuvent s'inscrire");
        }
        Participant participant = (Participant) personne;

        Creneau creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new IllegalArgumentException("Créneau non trouvé"));

        // Vérifier si déjà inscrit à ce créneau
        if (inscriptionRepository.existsByParticipantAndCreneau(participant, creneau)) {
            throw new IllegalArgumentException("Vous êtes déjà inscrit à ce créneau");
        }

        Inscription inscription = new Inscription();
        inscription.setParticipant(participant);
        inscription.setCreneau(creneau);
        inscription.setStatut(StatutInscription.EN_ATTENTE);
        inscription.setDateInscription(LocalDateTime.now());

        inscriptionRepository.save(inscription);
    }

    /**
     * ✅ Créer une inscription (ancien contrôleur - par ID)
     */
    @Transactional
    public Inscription creerInscription(Long participantId, Long creneauId) {
        Personne personne = personneRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant non trouvé"));

        if (!(personne instanceof Participant)) {
            throw new IllegalArgumentException("Seuls les participants peuvent s'inscrire");
        }
        Participant participant = (Participant) personne;

        Creneau creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new IllegalArgumentException("Créneau non trouvé"));

        if (inscriptionRepository.existsByParticipantIdAndCreneauId(participantId, creneauId)) {
            throw new IllegalArgumentException("Déjà inscrit à ce créneau");
        }

        Inscription inscription = new Inscription(participant, creneau);
        return  inscriptionRepository.save(inscription);
    }

    /**
     * Se désinscrire d'un atelier (via ID inscription)
     */
    @Transactional
    public void desinscrire(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscription non trouvée"));

        if (inscription.getStatut() != StatutInscription.EN_ATTENTE) {
            throw new IllegalArgumentException("Vous ne pouvez vous désinscrire que des inscriptions en attente");
        }

        inscriptionRepository.delete(inscription);
    }

    /**
     * ✅ Supprimer une inscription (ancien contrôleur - avec vérification participant)
     */
    @Transactional
    public void supprimerInscription(Long participantId, Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscription non trouvée"));

        if (!inscription.getParticipant().getId().equals(participantId)) {
            throw new IllegalArgumentException("Vous ne pouvez supprimer que vos propres inscriptions");
        }

        inscriptionRepository.delete(inscription);
    }

    // ========== STATISTIQUES ==========

    /**
     * Obtenir les statistiques (pour l'admin dashboard)
     */
    public InscriptionStats getStats() {
        long total = inscriptionRepository.count();
        long enAttente = inscriptionRepository.countByStatut(StatutInscription.EN_ATTENTE);
        long validees = inscriptionRepository.countByStatut(StatutInscription.VALIDEE);

        return new InscriptionStats(total, enAttente, validees);
    }

    // Classe interne pour les statistiques
    public static class InscriptionStats {
        private long total;
        private long enAttente;
        private long validees;

        public InscriptionStats(long total, long enAttente, long validees) {
            this.total = total;
            this.enAttente = enAttente;
            this.validees = validees;
        }

        public long getTotal() { return total; }
        public long getEnAttente() { return enAttente; }
        public long getValidees() { return validees; }
    }
}