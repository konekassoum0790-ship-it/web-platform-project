package com.example.fetescience.repository;

import com.example.fetescience.model.Atelier;
import com.example.fetescience.model.Creneau;
import com.example.fetescience.model.Inscription;
import com.example.fetescience.model.Participant;
import com.example.fetescience.model.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // Trouver toutes les inscriptions d'un participant
    List<Inscription> findByParticipant(Participant participant);

    // Trouver toutes les inscriptions d'un participant par son ID
    List<Inscription> findByParticipantId(Long participantId);

    // ✅ Vérifier si un participant est déjà inscrit à un créneau
    boolean existsByParticipantAndCreneau(Participant participant, Creneau creneau);

    // ✅ Vérifier si un participant est déjà inscrit à un atelier (via ses créneaux)
    boolean existsByParticipantAndCreneau_Atelier(Participant participant, Atelier atelier);

    // Trouver toutes les inscriptions pour un atelier donné
    List<Inscription> findByCreneau_Atelier(Atelier atelier);

    // Compter le nombre d'inscriptions pour un participant
    long countByParticipant(Participant participant);

    // Vérifier si une inscription existe par participant et créneau ID
    boolean existsByParticipantIdAndCreneauId(Long participantId, Long creneauId);

    // ✅ Compter par statut
    long countByStatut(StatutInscription statut);
}