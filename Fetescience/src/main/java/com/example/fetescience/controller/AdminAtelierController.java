package com.example.fetescience.controller;

import com.example.fetescience.model.*;
import com.example.fetescience.repository.AnimateurRepository;
import com.example.fetescience.repository.AtelierRepository;
import com.example.fetescience.repository.CreneauRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/ateliers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAtelierController {

    private final AtelierRepository atelierRepository;
    private final CreneauRepository creneauRepository;
    private final AnimateurRepository animateurRepository;

    public AdminAtelierController(AtelierRepository atelierRepository,
                                  CreneauRepository creneauRepository,
                                  AnimateurRepository animateurRepository) {
        this.atelierRepository = atelierRepository;
        this.creneauRepository = creneauRepository;
        this.animateurRepository = animateurRepository;
    }

    @GetMapping
    public String gererAteliers(Model model, HttpSession session) {

        Personne user = (Personne) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        if (!(user.getRole() == Role.ADMIN)) {
            return "redirect:/";
        }

        List<Atelier> ateliers = atelierRepository.findAll();
        List<Creneau> tousLesCreneaux = creneauRepository.findAll();
        List<Animateur> animateurs = animateurRepository.findAll();

        long totalCreneaux = tousLesCreneaux.size();
        long creneauxComplets = tousLesCreneaux.stream()
                .filter(Creneau::isComplet)
                .count();
        long creneauxDisponibles = totalCreneaux - creneauxComplets;

        model.addAttribute("ateliers", ateliers);
        model.addAttribute("totalCreneaux", totalCreneaux);
        model.addAttribute("creneauxComplets", creneauxComplets);
        model.addAttribute("creneauxDisponibles", creneauxDisponibles);
        model.addAttribute("animateurs", animateurs);

        return "admin_ateliers";
    }

    @PostMapping("/creneaux/ajouter")
    public String ajouterCreneau(@RequestParam Long atelierId,
                                 @RequestParam int horaireDebut,
                                 @RequestParam int duree,
                                 @RequestParam String lieu,
                                 @RequestParam int capacite,
                                 RedirectAttributes redirectAttributes) {
        try {
            Atelier atelier = atelierRepository.findById(atelierId)
                    .orElseThrow(() -> new IllegalArgumentException("Atelier introuvable"));

            Creneau nouveauCreneau = new Creneau(horaireDebut, duree, lieu, capacite);
            atelier.ajouterCreneau(nouveauCreneau);

            creneauRepository.save(nouveauCreneau);
            atelierRepository.save(atelier);

            redirectAttributes.addFlashAttribute("success",
                    "Créneau ajouté avec succès : " + horaireDebut + "h à " + lieu);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de l'ajout du créneau : " + e.getMessage());
        }

        return "redirect:/admin/ateliers";
    }

    @PostMapping("/creneaux/modifier")
    public String modifierCreneau(@RequestParam Long creneauId,
                                  @RequestParam int horaireDebut,
                                  @RequestParam int duree,
                                  @RequestParam String lieu,
                                  @RequestParam int capacite,
                                  RedirectAttributes redirectAttributes) {
        try {
            Creneau creneau = creneauRepository.findById(creneauId)
                    .orElseThrow(() -> new IllegalArgumentException("Créneau introuvable"));

            int nbInscriptions = creneau.getInscriptions().size();
            if (capacite < nbInscriptions) {
                redirectAttributes.addFlashAttribute("error",
                        "Impossible de réduire la capacité en dessous de " + nbInscriptions +
                                " (nombre d'inscriptions actuelles)");
                return "redirect:/admin/ateliers";
            }

            creneau.setHoraireDebut(horaireDebut);
            creneau.setDuree(duree);
            creneau.setLieu(lieu);
            creneau.setCapacite(capacite);
            creneau.setStatut(creneau.isComplet());

            creneauRepository.save(creneau);

            redirectAttributes.addFlashAttribute("success",
                    "Créneau modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la modification : " + e.getMessage());
        }

        return "redirect:/admin/ateliers";
    }

    @PostMapping("/creneaux/supprimer/{id}")
    public String supprimerCreneau(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            Creneau creneau = creneauRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Créneau introuvable"));

            if (!creneau.getInscriptions().isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "Impossible de supprimer un créneau avec des inscriptions actives. " +
                                "Veuillez d'abord gérer les " + creneau.getInscriptions().size() + " inscription(s).");
                return "redirect:/admin/ateliers";
            }

            creneauRepository.delete(creneau);
            redirectAttributes.addFlashAttribute("success",
                    "Créneau supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la suppression : " + e.getMessage());
        }

        return "redirect:/admin/ateliers";
    }


    // --- GESTION ATELIERS (New/Updated) ---

    @PostMapping("/ajouter")
    public String ajouterAtelier(@RequestParam String titre,
                                 @RequestParam String description,
                                 @RequestParam(required = false) Long animateurId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Atelier nouvelAtelier = new Atelier(titre);
            nouvelAtelier.setDescription(description);

            if (animateurId != null) {
                Animateur animateur = animateurRepository.findById(animateurId)
                        .orElseThrow(() -> new IllegalArgumentException("Animateur introuvable"));
                nouvelAtelier.setAnimateur(animateur);
            }

            atelierRepository.save(nouvelAtelier);

            redirectAttributes.addFlashAttribute("success",
                    "Atelier '" + titre + "' créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la création de l'atelier : " + e.getMessage());
        }

        return "redirect:/admin/ateliers";
    }

    @PostMapping("/modifier")
    public String modifierAtelier(@RequestParam Long id,
                                  @RequestParam String titre,
                                  @RequestParam String description,
                                  @RequestParam(required = false) Long animateurId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Atelier atelier = atelierRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Atelier introuvable"));

            atelier.setTitre(titre);
            atelier.setDescription(description);

            if (animateurId != null) {
                Animateur animateur = animateurRepository.findById(animateurId)
                        .orElseThrow(() -> new IllegalArgumentException("Animateur introuvable"));
                atelier.setAnimateur(animateur);
            } else {
                atelier.setAnimateur(null);
            }

            atelierRepository.save(atelier);
            redirectAttributes.addFlashAttribute("success", "Atelier modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur modification atelier : " + e.getMessage());
        }
        return "redirect:/admin/ateliers";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerAtelier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Atelier atelier = atelierRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Atelier introuvable"));

            // Note: CascadeType.ALL handles creneaux deletion, but check constraints if necessary
            atelierRepository.delete(atelier);

            redirectAttributes.addFlashAttribute("success", "Atelier supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur suppression atelier : " + e.getMessage());
        }
        return "redirect:/admin/ateliers";
    }
}