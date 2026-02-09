package com.example.fetescience.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
public class Animateur extends Personne {

    @OneToMany(mappedBy = "animateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Atelier> ateliers = new HashSet<>();

    public Animateur() {
        super();
        this.setRole(Role.ANIMATEUR);
    }

    public Animateur(String nom, String email, String password) {
        super(nom, email, password, Role.ANIMATEUR);
    }

    public void ajouterAtelier(Atelier atelier) {
        ateliers.add(atelier);
        atelier.setAnimateur(this);
    }

    public void retirerAtelier(Atelier atelier) {
        ateliers.remove(atelier);
        atelier.setAnimateur(null);
    }

    // --- CRITICAL FOR SETS ---
    // Sets rely on equals/hashCode to know if an item is already in the list.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animateur)) return false;
        Animateur other = (Animateur) o;
        // Use ID if available, otherwise strict object equality is safer than just false
        return this.getId() != null && this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        // Returning a constant is a standard JPA trick.
        // It prevents the object from "getting lost" in the Set if its ID changes after saving.
        return 31;
    }
}