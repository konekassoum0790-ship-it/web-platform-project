package com.example.fetescience.controller;

import com.example.fetescience.model.Inscription;
import com.example.fetescience.model.Personne;
import com.example.fetescience.model.Role;
import com.example.fetescience.service.InscriptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final InscriptionService inscriptionService;

    public AdminController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @GetMapping("/inscriptions")
    public String gererInscriptions(Model model, HttpSession session) {

        Personne user = (Personne) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        if (!(user.getRole() == Role.ADMIN)) {
            return "redirect:/";
        }
        List<Inscription> inscriptions = inscriptionService.getAllInscriptions();
        model.addAttribute("inscriptions", inscriptions);
        return "admin_inscriptions";
    }

    @PostMapping("/inscriptions/valider/{id}")
    public String validerInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Uses the service method we created
            inscriptionService.accepterInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription validée !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/inscriptions";
    }

    @PostMapping("/inscriptions/refuser/{id}")
    public String refuserInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.refuserInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription refusée !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/inscriptions";
    }
}