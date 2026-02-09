package com.example.fetescience.service;

import com.example.fetescience.model.Atelier;
import com.example.fetescience.model.Creneau;
import com.example.fetescience.repository.CreneauRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CreneauService {

    private final CreneauRepository repo;

    public CreneauService(CreneauRepository repo) {
        this.repo = repo;
    }

    public Creneau create(Creneau c) {
        return repo.save(c);
    }

    public List<Creneau> listAll() {
        return repo.findAll();
    }

    // AJOUT : Méthode pour récupérer un créneau spécifique
    public Creneau getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Créneau introuvable"));
    }

    public Creneau addCreneauToAtelier(Atelier atelier, Creneau creneau) {
        for (Creneau existing : atelier.getCreneaux()) {
            if (existing.chevauche(creneau)) {
                throw new RuntimeException("Chevauchement de créneaux !");
            }
        }
        atelier.ajouterCreneau(creneau);
        return repo.save(creneau);
    }

    public List<Atelier> ateliersAvecCreneauLibre(int horaire) {
        List<Creneau> libres = repo.findByHoraireDebutLessThanEqualAndStatutFalse(horaire);
        List<Atelier> ateliers = new ArrayList<>();
        for (Creneau c : libres) {
            if (!ateliers.contains(c.getAtelier())) {
                ateliers.add(c.getAtelier());
            }
        }
        return ateliers;
    }
}