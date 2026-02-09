package com.example.fetescience.repository;

import com.example.fetescience.model.Atelier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtelierRepository extends JpaRepository<Atelier, Long> {
    Optional<Atelier> findByTitre(String titre);
}
