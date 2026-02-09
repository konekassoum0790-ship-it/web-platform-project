package com.example.fetescience.controller;

import com.example.fetescience.model.Role;
import com.example.fetescience.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nom,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String role,
                           Model model) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            authService.registerUser(nom, email, password, userRole);
            model.addAttribute("success", "Compte créé avec succès ! En attente de validation.");
            return "redirect:login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    // ✅ SUPPRIMÉ : La redirection est maintenant gérée par SecurityConfig
    // @GetMapping("/default-redirect") était en conflit
}