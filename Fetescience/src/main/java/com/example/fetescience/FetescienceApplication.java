package com.example.fetescience;

import com.example.fetescience.model.*;
import com.example.fetescience.service.*;
import com.example.fetescience.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FetescienceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FetescienceApplication.class, args);
    }

    @Bean
    public CommandLineRunner testServices(AnimateurService animateurService,
                                          AtelierService atelierService,
                                          CreneauService creneauService,
                                          ParticipantService participantService,
                                          AuthService authService,
                                          PersonneRepository personneRepo,
                                          CreneauRepository creneauRepo,
                                          AtelierRepository atelierRepo) {
        return (args) -> {

            try{

                Participant admin = new Participant("Dobrina Botcheva", "dobrina.boltcheva@univ-lorraine.fr", "petitchienmignon");
                admin.setRole(Role.ADMIN);

                authService.registerUser(admin.getNom(), admin.getEmail(), admin.getPassword(), admin.getRole());
                System.out.println(" Admin créé : dobrina.boltcheva@univ-lorraine.fr / mot de passe");

            } catch (IllegalArgumentException e) {
                System.out.println("L'admin existe déjà");
            }




            /*System.out.println("🧹 NETTOYAGE DE LA BASE DE DONNÉES...");

            // 1. Delete Children (Creneaux)
            creneauRepo.deleteAll();

            // 2. Delete Ateliers (Linked to Animateurs)
            atelierRepo.deleteAll();

            // 3. Delete ALL Users (Animateurs + Participants) via the Parent Table
            personneRepo.deleteAll();

            System.out.println("✨ Base vide. Début de l'insertion...");
            System.out.println("\n⚡⚡⚡ DÉBUT DU TEST INTÉGRATION (AVEC AUTH) ⚡⚡⚡\n");

            // --- 1. ANIMATEURS ---
            // Constructeur : Nom, Email, Password
            Animateur anim1 = new Animateur("Marie Curie", "marie@science.com", "radium");
            animateurService.create(anim1);

            Animateur anim2 = new Animateur("Albert Einstein", "albert@science.com", "mc2");
            animateurService.create(anim2);

            // --- 2. ATELIERS ---
            Atelier atelier1 = new Atelier("Physique Quantique");
            atelier1.setAnimateur(anim1);
            atelierService.create(atelier1);

            Atelier atelier2 = new Atelier("Relativité");
            atelier2.setAnimateur(anim2);
            atelierService.create(atelier2);

            // --- 3. CRENEAUX ---
            Creneau c1 = new Creneau(10, 60, "Amphi A", 20);
            creneauService.addCreneauToAtelier(atelier1, c1);
            Creneau c2 = new Creneau(11, 15, "Amphi B", 1);
            creneauService.addCreneauToAtelier(atelier1, c2);
            Creneau c3 = new Creneau(10, 60, "Salle E406", 0);
            creneauService.addCreneauToAtelier(atelier2, c3);

            // --- 4. PARTICIPANTS ---
            System.out.println("--- Création des Participants ---");
            Participant p1 = new Participant("Alice", "alice@test.com", "passAlice");
            participantService.create(p1);

            Participant p2 = new Participant("Bob", "bob@test.com", "passBob");
            participantService.create(p2);

            Participant p3 = new Participant("Charlie", "charlie@test.com", "passCharlie");
            participantService.create(p3);

            System.out.println("✅ Participants créés : " + p1.getNom() + ", " + p2.getNom() + ", " + p3.getNom());

            // --- 5. INSCRIPTIONS ---
          //  participantService.inscrire(p1.getId(), c1.getId());
            //participantService.inscrire(p2.getId(), c1.getId());
            System.out.println("✅ Inscriptions effectuées.");

            // --- 6. TEST AUTHENTIFICATION ---
            System.out.println("\n🔐 --- Test de l'AuthService ---");

            // Test A: Login Valid (Animateur)
            System.out.print("👉 Test Login 'marie@science.com' (Animateur) : ");
            Personne user1 = authService.authenticate("marie@science.com", "radium");
            if (user1 != null) {
                System.out.println("✅ SUCCÈS - Connecté en tant que " + user1.getRole());
            } else {
                System.out.println("❌ ÉCHEC");
            }

            // Test B: Login Valid (Participant)
            System.out.print("👉 Test Login 'alice@test.com' (Participant) : ");
            Personne user2 = authService.authenticate("alice@test.com", "passAlice");
            if (user2 != null) {
                System.out.println("✅ SUCCÈS - Connecté en tant que " + user2.getRole());
            } else {
                System.out.println("❌ ÉCHEC");
            }

            // Test C: Bad Password
            System.out.print("👉 Test Mauvais Mot de Passe : ");
            Personne user3 = authService.authenticate("alice@test.com", "mauvaispass");
            if (user3 == null) {
                System.out.println("✅ SUCCÈS (Login rejeté correctement)");
            } else {
                System.out.println("❌ ÉCHEC (L'utilisateur ne devrait pas être connecté !)");
            }

            System.out.println("\n🧪 TEST VALIDATION EMAIL 🧪");
            try {
                // Attempt to create a user with a bad email
                Participant fake = new Participant("Hacker", "not-an-email", "1234");
                participantService.create(fake);
                System.out.println("❌ ERREUR : L'email invalide a été accepté (ce n'est pas normal)");
            } catch (Exception e) {
                System.out.println("✅ SUCCÈS : L'email invalide a été bloqué !");
                System.out.println("   Message d'erreur : " + e.getMessage());
            }
            System.out.println("\n✨✨✨ TEST TERMINÉ ✨✨✨");*/

            System.out.println("\n✨✨✨ TEST TERMINÉ ✨✨✨");
            System.out.println("➡️ Application prête sur http://localhost:8081");
            System.out.println("➡️ Page connexion : http://localhost:8081/login");
            System.out.println("➡️ Page admin : http://localhost:8081/admin/inscriptions");
        };
    }
}
