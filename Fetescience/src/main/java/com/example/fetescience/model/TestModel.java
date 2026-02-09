/*package com.example.fetescience.model;


public class TestModel {

    public static void main(String[] args) {
        System.out.println("=== TEST LOCAL (NOUVELLE STRUCTURE) ===\n");

        // ---------------------------------------------------------
        // 1. ANIMATEURS (Nouveau Constructeur : Nom, Email, Password)
        // ---------------------------------------------------------
        // On met des emails/passwords bidons car on ne teste pas le login ici
        Animateur anim1 = new Animateur("Sophie L.", "sophie@test.com", "1234");
        Animateur anim2 = new Animateur("Marc D.", "marc@test.com", "5678");

        System.out.println("1. Animateurs créés : " + anim1.getNom() + ", " + anim2.getNom());

        // ---------------------------------------------------------
        // 2. ATELIERS (Pas de changement)
        // ---------------------------------------------------------
        Atelier atelier1 = new Atelier("Chimie Amusante");
        Atelier atelier2 = new Atelier("Astronomie pour tous");

        anim1.ajouterAtelier(atelier1);
        anim2.ajouterAtelier(atelier2);

        System.out.println("\n2. Vérification liens :");
        System.out.println("   - Atelier 1 géré par : " + atelier1.getAnimateur().getNom());

        // ---------------------------------------------------------
        // 3. CRENEAUX (Pas de changement)
        // ---------------------------------------------------------
        Creneau c1 = new Creneau(9, 60, "Salle A", 2);
        Creneau c2 = new Creneau(10, 60, "Salle A", 10);

        atelier1.ajouterCreneau(c1);
        atelier1.ajouterCreneau(c2);

        // ---------------------------------------------------------
        // 4. PARTICIPANTS (Nouveau Constructeur aussi)
        // ---------------------------------------------------------
        Participant p1 = new Participant("Alice", "alice@test.com", "passAlice");
        Participant p2 = new Participant("Bob", "bob@test.com", "passBob");

        // Ou utiliser le constructeur vide + setters (grâce à @Setter sur la classe)
        Participant p3 = new Participant();
        p3.setNom("Charlie");
        p3.setEmail("charlie@test.com");

        // ---------------------------------------------------------
        // 5. INSCRIPTIONS
        // ---------------------------------------------------------
        System.out.println("\n5. Test Inscriptions :");

        // P1 s'inscrit
        //p1.addCreneau(c1);
        System.out.println("   - " + p1.getNom() + " inscrit à " + c1.getAtelier().getTitre());

        // Vérification de l'héritage (Méthodes de Personne)
        System.out.println("\n6. Vérification Héritage :");
        System.out.println("   - Rôle de Alice : " + p1.getRole()); // Doit afficher "PARTICIPANT"
        System.out.println("   - Email de Marc : " + anim2.getEmail());

        System.out.println("\n=== FIN DU TEST ===");
    }
}*/