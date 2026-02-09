package com.example.fetescience.service;

import com.example.fetescience.model.Personne;
import com.example.fetescience.repository.PersonneRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonneRepository personneRepository;

    public CustomUserDetailsService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Personne personne = personneRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));

        if (!personne.isAccountVerified()) {
            throw new UsernameNotFoundException("Compte non vérifié. Contactez un administrateur.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + personne.getRole().name()));

        return User.builder()
                .username(personne.getEmail())
                .password(personne.getPassword())
                .authorities(authorities)
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .disabled(!personne.isAccountVerified())
                .build();
    }
}
