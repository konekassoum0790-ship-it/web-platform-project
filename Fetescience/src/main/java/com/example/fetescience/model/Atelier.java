package com.example.fetescience.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
public class Atelier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    // Description with 500 char limit
    // @Column creates a VARCHAR(500) in the DB
    // @Size performs validation in Java before saving
    @Column(length = 500)
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    @ManyToOne
    @JoinColumn(name = "animateur_id")
    private Animateur animateur;

    @OneToMany(mappedBy = "atelier", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Creneau> creneaux = new HashSet<>();

    public Atelier() {}

    public Atelier(String titre) {
        this.titre = titre;
    }


    public void ajouterCreneau(Creneau c) {
        creneaux.add(c);
        c.setAtelier(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Atelier)) return false;
        Atelier atelier = (Atelier) o;
        return this.id != null && this.id.equals(atelier.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
