package com.example.fetescience.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean accountVerified = false;

    public Personne() {}

    public Personne(String nom, String email, String password, Role role) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.accountVerified = false;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAccountVerified(boolean accountVerified) {
        this.accountVerified = accountVerified;
    }
}
