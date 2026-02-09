package com.example.fetescience.controller;

import com.example.fetescience.model.Animateur;
import com.example.fetescience.repository.AnimateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnimateurController {

    private final AnimateurRepository animateurRepository;

    public AnimateurController(AnimateurRepository animateurRepository) {
        this.animateurRepository = animateurRepository;
    }

    // GET http://localhost:8081/animateurs
    @GetMapping("/animateurs")
    public List<Animateur> getAllAnimateurs() {
        return animateurRepository.findAll();
    }

    // GET http://localhost:8081/animateurs/1
    @GetMapping("/animateurs/{id}")
    public ResponseEntity<Animateur> getAnimateurById(@PathVariable Long id) {
        return animateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET http://localhost:8081/animateurs/search?nom=Dupont
    @GetMapping("/animateurs/search")
    public ResponseEntity<Animateur> getAnimateurByNom(@RequestParam String nom) {
        return animateurRepository.findByNom(nom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
