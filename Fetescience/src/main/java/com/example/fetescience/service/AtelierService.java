package com.example.fetescience.service;

import com.example.fetescience.model.Atelier;
import com.example.fetescience.model.Creneau;
import com.example.fetescience.repository.AtelierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class AtelierService {
    private final AtelierRepository repo;

    public AtelierService(AtelierRepository repo) {
        this.repo = repo;
    }

    public Atelier create(Atelier a) {
        return repo.save(a);
    }

    public List<Atelier> listAll() {
        return repo.findAll();
    }

    public Atelier getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Atelier not found"));
    }

    // Logic to update location for all slots
    public Atelier updateLieu(Long id, String newLieu) {
        Atelier atelier = getById(id);
        for (Creneau c : atelier.getCreneaux()) {
            c.setLieu(newLieu);
        }
        return repo.save(atelier);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}