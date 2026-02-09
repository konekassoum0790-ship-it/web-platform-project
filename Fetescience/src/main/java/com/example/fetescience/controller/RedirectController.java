package com.example.fetescience.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {
//    /*
//
//    /**
//     * ✅ Redirection après connexion selon le rôle
//     */
//    @GetMapping("/default-redirect")
//    public String defaultRedirect(Authentication authentication) {
//        // Vérifier si l'utilisateur est ADMIN
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
//            return "redirect:/admin/dashboard";
//        }
//
//        // Vérifier si l'utilisateur est PARTICIPANT
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PARTICIPANT"))) {
//            return "redirect:/inscriptions/mes-inscriptions";
//        }
//
//        // Par défaut (ne devrait pas arriver)
//        return "redirect:/";
//    }
}