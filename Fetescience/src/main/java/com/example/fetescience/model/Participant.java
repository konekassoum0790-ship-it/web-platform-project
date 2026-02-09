package com.example.fetescience.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
public class Participant extends Personne{


    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscription> inscriptions = new HashSet<>();

    public Participant() {
        super();
        this.setRole(Role.PARTICIPANT);
    }

    public Participant(String nom, String email, String password) {
        super(nom, email, password, Role.PARTICIPANT);
    }

    // Helper methods for synchronization
    public void ajouterInscription(Inscription i) {
        inscriptions.add(i);
        i.setParticipant(this);
    }

    // Replaces the old removeCreneau logic
    public void supprimerInscription(Inscription i) {
        if (i != null && inscriptions.contains(i)) {
            this.inscriptions.remove(i);
            i.setParticipant(null);

            // 3. Remove from the Creneau's list (Bidirectional consistency)
            if (i.getCreneau() != null) {
                i.getCreneau().getInscriptions().remove(i);
            }
        }
    }

    public void desinscrireDuCreneau(Creneau c) {
        // Find the inscription that links THIS participant to THAT creneau
        Inscription inscriptionToRemove = null;

        for (Inscription i : this.inscriptions) {
            if (i.getCreneau().equals(c)) {
                inscriptionToRemove = i;
                break;
            }
        }

        // If found, delete it safely
        if (inscriptionToRemove != null) {
            supprimerInscription(inscriptionToRemove);
        }
    }

/*@Override
public boolean equals(Object o) {
    if (this == o) return true;  // même object
    if (!(o instanceof Participant)) return false;
    Participant participant = (Participant) o;
    return this.getId() != null && this.getId().equals(participant.getId());
}*/

/*@Override
public int hashCode() {
    return id != null ? id.hashCode() : 0;
}*/
}
