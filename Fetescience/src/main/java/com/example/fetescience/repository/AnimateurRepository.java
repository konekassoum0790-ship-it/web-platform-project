package com.example.fetescience.repository;

import com.example.fetescience.model.Animateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AnimateurRepository extends JpaRepository<Animateur, Long> {
    Optional<Animateur> findByNom(String nom);
}