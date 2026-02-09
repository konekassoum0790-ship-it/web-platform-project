package com.example.fetescience.repository;

import com.example.fetescience.model.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Long> {
    Optional<Personne> findByEmail(String email);
}