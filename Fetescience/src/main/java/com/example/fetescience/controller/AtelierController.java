package com.example.fetescience.controller;

import com.example.fetescience.model.Animateur;
import com.example.fetescience.model.Atelier;
import com.example.fetescience.model.Creneau;
import com.example.fetescience.service.AtelierService;
import com.example.fetescience.service.CreneauService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/animateur/ateliers") // Groups all these URLs under this prefix
public class AtelierController {

    private final AtelierService atelierService;
    private final CreneauService creneauService;

    public AtelierController(AtelierService atelierService, CreneauService creneauService) {
        this.atelierService = atelierService;
        this.creneauService = creneauService;
    }
/// ***************************** Gestion de creation d'atelier ******************  (CREATE, EDIT, DELETE)
    // 1. Display the Form (GET) for creation and edit
@GetMapping({"/nouveau", "/{id}/modifier"})
    public String afficherFormulaire(
            @PathVariable(required = false) Long id,
            HttpSession session,
            Model model
    ) {
        if (!isAnimateur(session)) return "redirect:/auth/login";

        Animateur loggedUser = (Animateur) session.getAttribute("user");
        Atelier atelier;

        if (id == null) {
            // Creation
            atelier = new Atelier();
        } else {
            //  Modification
            atelier = atelierService.getById(id);

            if (!atelier.getAnimateur().getId().equals(loggedUser.getId())) {
                return "redirect:/animateur_page"; //Security check
            }
        }

        model.addAttribute("atelier", atelier);
        return "nouvel_atelier";
    }
    // 2. Process the Form (POST)
    @PostMapping
    public String sauvegarderAtelier(@ModelAttribute Atelier atelier, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAnimateur(session)) return "redirect:/auth/login";

        Animateur loggedUser = (Animateur) session.getAttribute("user");

        if (atelier.getId() != null) {
            // === UPDATE MODE ===
            Atelier existingAtelier = atelierService.getById(atelier.getId());

            // Security Check
            if (!existingAtelier.getAnimateur().getId().equals(loggedUser.getId())) {
                return "redirect:/animateur_page";
            }

            // Update fields
            existingAtelier.setTitre(atelier.getTitre());
            existingAtelier.setDescription(atelier.getDescription());

            atelierService.create(existingAtelier); // Save updates
            redirectAttributes.addFlashAttribute("success", "Atelier modifié avec succès.");
            return "redirect:/animateur/ateliers/" + existingAtelier.getId() + "/gestion";

        } else {
            // === CREATE MODE ===
            atelier.setAnimateur(loggedUser);
            Atelier savedAtelier = atelierService.create(atelier);
            redirectAttributes.addFlashAttribute("success", "Atelier créé avec succès.");
            return "redirect:/animateur/ateliers/" + savedAtelier.getId() + "/gestion";
        }
    }

    // Delete Atelier Endpoint
    @PostMapping("/{id}/supprimer")
    public String supprimerAtelier(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // 1. Security Check
        if (!isAnimateur(session)) {
            return "redirect:/auth/login";
        }

        // Fetch Atelier to check ownership
        Atelier atelier = atelierService.getById(id);
        Animateur loggedUser = (Animateur) session.getAttribute("user");

        // Ownership Check (Critical Security)
        if (atelier == null || !atelier.getAnimateur().getId().equals(loggedUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "Action non autorisée.");
            return "redirect:/animateur_page";
        }

        // 4. Perform Delete
        atelierService.delete(id);

        redirectAttributes.addFlashAttribute("success", "L'atelier '" + atelier.getTitre() + "' a été supprimé.");
        return "redirect:/animateur_page";
    }




    /// ***************************** Gestion d'ajout de creneau à l'atelier ******************

    // The Management Page (GET)
    // Shows the Atelier details + List of Creneaux + Form to add a new one
    @GetMapping("/{id}/gestion")
    public String gererAtelier(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAnimateur(session)) {
            return "redirect:/auth/login";
        }

        // Fetch the Atelier
        Atelier atelier = atelierService.getById(id);

        // SECURITY : If they try to manage someone else's work, kick them out
        Animateur loggedUser = (Animateur) session.getAttribute("user");
        if (!atelier.getAnimateur().getId().equals(loggedUser.getId())) {
            return "redirect:/animateur_page";
        }

        model.addAttribute("atelier", atelier);
        // We create an empty Creneau for the "Add New" form
        model.addAttribute("nouveauCreneau", new Creneau());

        return "gestion_atelier";
    }

    // 4. Add a Creneau (POST)
    @PostMapping("/{atelierId}/creneaux")
    public String sauvegarderCreneau(@PathVariable Long atelierId, @ModelAttribute Creneau creneauForm,
                                      HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAnimateur(session)) {
            return "redirect:/auth/login";
        }

        Atelier atelier = atelierService.getById(atelierId);
        // Check is the animateur editing/saving is the user himself
        Animateur loggedUser = (Animateur) session.getAttribute("user");
        if (!atelier.getAnimateur().getId().equals(loggedUser.getId())) {
            return "redirect:/animateur_page"; // TENTATIVE DE HACK détectée
        }

        try {


            if (creneauForm.getId() != null) {
                // === MODE MODIFICATION ===
                // On récupère le créneau existant en base pour ne pas perdre les inscriptions
                Creneau creneauExistant = creneauService.getById(creneauForm.getId());

                // 🚨 SÉCURITÉ CRITIQUE 🚨
                // On vérifie que le créneau envoyé via le champ caché appartient bien
                // à l'atelier de l'URL (qui appartient à l'animateur connecté).
                if (!creneauExistant.getAtelier().getId().equals(atelierId)) {
                    // Si l'ID ne correspond pas, c'est que quelqu'un a trafiqué le formulaire HTML
                    throw new RuntimeException("Tentative de modification illégale !");
                }

                // On met à jour uniquement les champs modifiables
                creneauExistant.setHoraireDebut(creneauForm.getHoraireDebut());
                creneauExistant.setDuree(creneauForm.getDuree());
                creneauExistant.setCapacite(creneauForm.getCapacite());
                creneauExistant.setLieu(creneauForm.getLieu());

                // On sauvegarde (le service gère le save)
                creneauService.create(creneauExistant);
            } else {

                // Use the helper method in your Atelier entity to link them
                atelier.ajouterCreneau(creneauForm);
                // Saving the Atelier will automatically save the new Creneau because of cascade = CascadeType.ALL in  model
                atelierService.create(atelier);
            }
        }
        catch (RuntimeException e) {
            // GÉRER LE "500"
            // Au lieu de planter, on renvoie l'utilisateur vers la page avec un message
            redirectAttributes.addFlashAttribute("error", "Erreur : Ce créneau n'existe plus ou est invalide.");
            return "redirect:/animateur/ateliers/" + atelierId + "/gestion";
        }

        return "redirect:/animateur/ateliers/" + atelierId + "/gestion";
    }



    // Helper method for Session Security
    private boolean isAnimateur(HttpSession session) {
        Object user = session.getAttribute("user");
        return user instanceof Animateur;
    }
}