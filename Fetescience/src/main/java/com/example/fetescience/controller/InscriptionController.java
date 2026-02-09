package com.example.fetescience.controller;

import com.example.fetescience.model.Inscription;
import com.example.fetescience.model.Participant;
import com.example.fetescience.model.Personne;
import com.example.fetescience.model.Role;
import com.example.fetescience.service.AtelierService;
import com.example.fetescience.service.InscriptionService;
import com.example.fetescience.service.ParticipantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller

public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final ParticipantService participantService;
    private final AtelierService atelierService;

    public InscriptionController(InscriptionService inscriptionService,
                                 ParticipantService participantService,
                                 AtelierService atelierService) {
        this.inscriptionService = inscriptionService;
        this.participantService = participantService;
        this.atelierService = atelierService;
    }

    // --- 1. SHOW FORM (With Pre-filled Name) ---
    @GetMapping("/nouvelle-inscription")
    public String afficherFormulaireInscription(Model model, HttpSession session) {
        // Load ateliers for the dropdown
        model.addAttribute("ateliers", atelierService.listAll());

        // Check if user is logged in
        Personne user = (Personne) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        // Pre-fill form if Participant
        if (user.getRole() == Role.PARTICIPANT) {
            model.addAttribute("preFilledName", user.getNom());
            return "nouvelle_inscription";
        } else if (user.getRole() == Role.ADMIN) {
            return "nouvelle_inscription"; // Admin can view form too
        }

        return "redirect:/";
    }

    // --- 2. PROCESS FORM (With Security & Service Return) ---
    @PostMapping("/inscrire_atelier")
    public String inscrireAtelier(
            @RequestParam String nom,
            @RequestParam Long creneauId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Personne user = (Personne) session.getAttribute("user");

            if (user == null) {
                return "redirect:/auth/login";
            }

            // SECURITY: If Participant, ensure they are registering themselves
            if (user.getRole() == Role.PARTICIPANT) {
                if (!user.getNom().equalsIgnoreCase(nom.trim())) {
                    throw new IllegalArgumentException("Erreur de sécurité : Nom ne correspond pas au compte connecté.");
                }
            }

            // Find Participant
            Participant participant = participantService.findByNom(nom.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Participant introuvable."));

            // ✅ Create Inscription and CAPTURE the result
            Inscription inscription = inscriptionService.creerInscription(
                    participant.getId(),
                    creneauId
            );

            // ✅ USE the result to show the specific workshop title
            redirectAttributes.addFlashAttribute("succes",
                    "Inscription réussie : " + inscription.getCreneau().getAtelier().getTitre());

            // Redirect to the clean 'My Inscriptions' URL
            return "redirect:/inscriptions/mes-inscriptions";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/nouvelle-inscription";
        }
    }

    // --- 3. SHOW MY INSCRIPTIONS (Canonical Endpoint) ---
    @GetMapping("/inscriptions/mes-inscriptions")
    public String afficherMesInscriptions(Model model, HttpSession session) {
        Personne user = (Personne) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/inscriptions";
        }

        try {
            Participant participant = participantService.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Inscription> inscriptions = inscriptionService.getInscriptionsByParticipant(user.getId());

            model.addAttribute("participant", participant);
            model.addAttribute("inscriptions", inscriptions);

            // Reuses the existing "inscriptions.html" template
            return "inscriptions";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            return "inscriptions";
        }
    }

    // --- 4. SHOW SPECIFIC PARTICIPANT (Admin Only) ---
    @GetMapping("/inscriptions/{participantId}")
    public String afficherInscriptionsParticipant(@PathVariable Long participantId, Model model, HttpSession session) {
        Personne user = (Personne) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        // Optimization: If user tries to access their own ID explicitly, redirect to canonical URL
        if (user.getId().equals(participantId)) {
            return "redirect:/inscriptions/mes-inscriptions";
        }

        // SECURITY: Only Admin can view other people's data
        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé.");
        }

        try {
            Participant participant = participantService.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("Participant introuvable"));

            List<Inscription> inscriptions = inscriptionService.getInscriptionsByParticipant(participantId);

            model.addAttribute("participant", participant);
            model.addAttribute("inscriptions", inscriptions);

            return "inscriptions";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            return "inscriptions";
        }
    }

    // --- 5. UNREGISTER (AJAX from deleteInscription.js) ---
    @DeleteMapping("/inscriptions/{id}")
    @ResponseBody
    public ResponseEntity<String> seDesinscrire(@PathVariable Long id, HttpSession session) {
        try {
            Personne user = (Personne) session.getAttribute("user");

            if (user == null) {
                return ResponseEntity.status(401).body("Vous devez être connecté.");
            }


            // Calls service: void supprimerInscription(Long participantId, Long inscriptionId)
            inscriptionService.supprimerInscription(user.getId(), id);

            // If no exception thrown, it was successful
            return ResponseEntity.ok("Désinscription réussie");

        } catch (IllegalArgumentException e) {
            // Handles "Not found" or "Not your inscription"
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }

    // Default redirect to the clean URL
    @GetMapping("/inscriptions")
    public String afficherInscriptionsDefaut() {
        return "redirect:/inscriptions/mes-inscriptions";
    }
}