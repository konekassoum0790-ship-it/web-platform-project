package com.example.fetescience.repository;

import com.example.fetescience.model.Creneau;
import com.example.fetescience.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
    List<Creneau> findByAtelierId(Long atelierId);

    List<Creneau> findByHoraireDebutLessThanEqualAndStatutFalse(int horaire);

    // FIX 1: Changed 'DateDebut' to 'HoraireDebut' (from your previous error)
    List<Creneau> findByAtelierIdOrderByHoraireDebutAsc(Long id);

    // FIX 2: Changed 'ParticipantsContaining' to 'Inscriptions_Participant'
    // This tells JPA: "Go into Inscriptions list, check the Participant field"
    List<Creneau> findByInscriptions_ParticipantOrderByHoraireDebutAsc(Participant participant);
}