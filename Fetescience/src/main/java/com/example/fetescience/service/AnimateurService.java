package com.example.fetescience.service;

import com.example.fetescience.model.Animateur;
import com.example.fetescience.repository.AnimateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class AnimateurService {
    private final AnimateurRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AnimateurService(AnimateurRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Animateur create(Animateur a) {
        if (a.getNom() == null || a.getNom().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        if (repo.findByNom(a.getNom()).isPresent()) {
            throw new IllegalArgumentException("Animateur exists!");
        }

        // ✅ Hasher le mot de passe
        a.setPassword(passwordEncoder.encode(a.getPassword()));

        // ✅ Activer le compte par défaut
        a.setAccountVerified(true);

        return repo.save(a);
    }

    public List<Animateur> listAll() {
        return repo.findAll();
    }

    public Animateur getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Animateur not found"));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}