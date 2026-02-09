package com.example.fetescience.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "creneau_id", nullable = false)
    private Creneau creneau;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statut;

    public Inscription() {
        this.dateInscription = LocalDateTime.now();
        this.statut = StatutInscription.EN_ATTENTE;
    }

    public Inscription(Participant participant, Creneau creneau) {
        this();
        this.participant = participant;
        this.creneau = creneau;
    }

    /**
     * Vérifie si l'inscription peut être annulée
     * (au moins 2 jours avant le créneau)
     */
    public boolean peutEtreAnnulee() {
        return true; // À améliorer avec la vraie logique de date
    }
}