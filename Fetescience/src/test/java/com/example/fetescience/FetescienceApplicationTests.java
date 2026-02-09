package com.example.fetescience;

import com.example.fetescience.model.*;
import com.example.fetescience.service.*;
import com.example.fetescience.repository.PersonneRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Annule les modifications en base de donnees a la fin de chaque test
class FetescienceApplicationTests {

    @Autowired
    private AnimateurService animateurService;

    @Autowired
    private AtelierService atelierService;

    @Autowired
    private CreneauService creneauService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Test 1 : Scenario Nominal
     * Nouveaux noms : Rosalind Franklin, Nikola Tesla, Victor
     */
    @Test
    void testScenarioGlobal() {
        System.out.println("TEST: Initialisation des donnees...");

        // --- 1. ANIMATEURS ---
        Animateur anim1 = new Animateur("Rosalind Franklin", "rosalind@science.com", "dna_helix");
        animateurService.create(anim1);

        assertNotNull(anim1.getId());
        assertTrue(passwordEncoder.matches("dna_helix", anim1.getPassword()));

        Animateur anim2 = new Animateur("Nikola Tesla", "nikola@science.com", "electricite");
        animateurService.create(anim2);

        // --- 2. ATELIERS ---
        Atelier atelier1 = new Atelier("Cristallographie");
        atelier1.setAnimateur(anim1);
        atelierService.create(atelier1);

        Atelier atelier2 = new Atelier("Bobine Tesla");
        atelier2.setAnimateur(anim2);
        atelierService.create(atelier2);

        // CORRECTION ICI : On ne vérifie plus la taille totale (= 2),
        // mais que NOS ateliers sont bien présents dans la liste.
        List<Atelier> tousLesAteliers = atelierService.listAll();
        // On vérifie qu'on trouve un atelier avec l'ID de atelier1
        assertTrue(tousLesAteliers.stream().anyMatch(a -> a.getId().equals(atelier1.getId())),
                "L'atelier Cristallographie devrait être présent en base");
        assertTrue(tousLesAteliers.stream().anyMatch(a -> a.getId().equals(atelier2.getId())),
                "L'atelier Bobine Tesla devrait être présent en base");


        // --- 3. CRENEAUX ---
        Creneau c1 = new Creneau(14, 60, "Labo Chimie", 20);
        creneauService.addCreneauToAtelier(atelier1, c1);

        Creneau c2 = new Creneau(15, 30, "Salle Rayons X", 1);
        creneauService.addCreneauToAtelier(atelier1, c2);

        Creneau c3 = new Creneau(10, 60, "Tour Wardenclyffe", 0);
        creneauService.addCreneauToAtelier(atelier2, c3);

        // IDEM POUR LES CRENEAUX : On vérifie juste que c3 existe et qu'il est complet
        assertNotNull(creneauService.getById(c3.getId()));
        assertTrue(c3.isStatut(), "Le creneau c3 devrait etre marque complet (capacite 0)");

        // --- 4. PARTICIPANTS ---
        Participant p1 = new Participant("Victor", "victor@test.com", "passVictor");
        participantService.create(p1);

        // --- 5. INSCRIPTIONS ---
        participantService.inscrire(p1.getId(), c1.getId());

        // Verifications finales
        Participant p1Updated = participantService.getById(p1.getId());
        // Ici c'est sûr car p1 est nouveau, il doit avoir exactement 1 inscription
        assertEquals(1, p1Updated.getInscriptions().size(), "Victor devrait avoir 1 inscription");

        Creneau c1Updated = creneauService.getById(c1.getId());
        // On vérifie qu'il y a au moins notre inscription
        assertFalse(c1Updated.getInscriptions().isEmpty());
        assertFalse(c1Updated.isStatut(), "Le creneau ne devrait pas etre complet");
    }

    /**
     * Test 2 : Inscription via AuthService
     * Nouveau nom : Sophie
     */
    @Test
    void testAuthServiceRegister() {
        System.out.println("TEST: Inscription AuthService...");

        // Inscription de Sophie via le service d'auth
        authService.registerUser("Sophie", "sophie@test.com", "azerty456", Role.PARTICIPANT);

        // Verification en base
        Personne sophie = personneRepository.findByEmail("sophie@test.com").orElse(null);
        assertNotNull(sophie, "Sophie devrait etre trouvee en base");
        assertEquals(Role.PARTICIPANT, sophie.getRole());
        assertTrue(sophie.isAccountVerified());

        // Verification cle : le mot de passe stocke doit correspondre au hash
        assertTrue(passwordEncoder.matches("azerty456", sophie.getPassword()), "Le mot de passe en base n'est pas correct");
    }

    /**
     * Test 3 : Regles Metier et Exceptions
     * Noms generiques (Copie, Court, Mendeleiev, Gamma, Delta)
     */
    @Test
    void testReglesMetier() {
        System.out.println("TEST: Regles Metier...");

        // 1. Email deja existant
        authService.registerUser("Original", "jamais_vu@test.com", "123456", Role.PARTICIPANT);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser("Copie", "jamais_vu@test.com", "123456", Role.PARTICIPANT);
        }, "Une exception doit etre levee si l'email existe deja");

        // 2. Mot de passe trop court
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser("Court", "court@test.com", "123", Role.PARTICIPANT);
        }, "Une exception doit etre levee si le mot de passe fait moins de 6 caracteres");

        // 3. Inscription impossible si complet
        // Setup : Atelier et Creneau de capacite 1
        Animateur anim = new Animateur("Mendeleiev", "tableau@test.com", "elements");
        animateurService.create(anim);

        Atelier atelier = new Atelier("Atelier Elements");
        atelier.setAnimateur(anim);
        atelierService.create(atelier);

        Creneau creneauUnique = new Creneau(14, 60, "Salle 101", 1);
        creneauService.addCreneauToAtelier(atelier, creneauUnique);

        // Gamma prend la place
        Participant p1 = new Participant("Gamma", "gamma@test.com", "password");
        participantService.create(p1);
        participantService.inscrire(p1.getId(), creneauUnique.getId());

        // Delta essaie de s'inscrire -> Doit echouer
        Participant p2 = new Participant("Delta", "delta@test.com", "password");
        participantService.create(p2);

        assertThrows(RuntimeException.class, () -> {
            participantService.inscrire(p2.getId(), creneauUnique.getId());
        }, "L'inscription devrait echouer car le creneau est complet");
    }
}