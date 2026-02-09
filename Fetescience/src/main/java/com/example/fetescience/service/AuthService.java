package com.example.fetescience.service;

import com.example.fetescience.model.*;
import com.example.fetescience.repository.AnimateurRepository;
import com.example.fetescience.repository.ParticipantRepository;
import com.example.fetescience.repository.PersonneRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PersonneRepository personneRepository;
    private final ParticipantRepository participantRepository;
    private final AnimateurRepository animateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PersonneRepository personneRepository,
                       ParticipantRepository participantRepository,
                       AnimateurRepository animateurRepository,
                       PasswordEncoder passwordEncoder) {
        this.personneRepository = personneRepository;
        this.participantRepository = participantRepository;
        this.animateurRepository = animateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String nom, String email, String password, Role role) {
        if (personneRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }

        String encodedPassword = passwordEncoder.encode(password);

        if (role == Role.PARTICIPANT) {
            Participant participant = new Participant(nom, email, encodedPassword);
            participant.setAccountVerified(true); // AJOUT : Auto-vérification
            participantRepository.save(participant);
        } else if (role == Role.ANIMATEUR) {
            Animateur animateur = new Animateur(nom, email, encodedPassword);
            animateur.setAccountVerified(true); // AJOUT : Auto-vérification
            animateurRepository.save(animateur);
        }
        else if (role == Role.ADMIN){
            Animateur admin = new Animateur(nom, email, encodedPassword);
            admin.setRole(Role.ADMIN);
            admin.setAccountVerified(true); // AJOUT : Auto-vérification
            animateurRepository.save(admin);
        }
        else {
            throw new IllegalArgumentException("Rôle invalide pour l'inscription");
        }
    }
}