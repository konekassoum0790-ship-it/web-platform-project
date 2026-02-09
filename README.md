# Fête de la Science - Système de Gestion d'Ateliers

## Vue d'ensemble du projet

Ce projet est une application web full-stack conçue pour numériser l'organisation de l'événement "Fête de la Science". Le système optimise la gestion des ateliers scientifiques, permettant aux participants (élèves, enseignants, grand public) de consulter les sessions disponibles et de s'inscrire à des créneaux horaires spécifiques. Il fournit également des outils administratifs pour les responsables d'ateliers (Animateurs) et les administrateurs afin de gérer les plannings et de valider les inscriptions.

Le projet est actuellement construit avec un backend **Java Spring Boot** et un frontend **Thymeleaf**. L'architecture est modulaire et pensée pour faciliter une future migration vers un client séparé en **Angular**.

## Architecture Technique

Le cœur de l'application repose sur une architecture n-tiers robuste utilisant l'écosystème Spring.

### 1. Backend & Structure du Code (Spring Boot)
L'application respecte le principe de séparation des préoccupations (SoC) à travers les couches suivantes :

* **Models (Entités JPA) :** Représentent la structure de la base de données sous forme d'objets Java.
    * Les entités principales incluent `Atelier`, `Creneau`, `Inscription`, `Participant` et `Animateur`.
    * Les relations (OneToMany, ManyToOne) sont configurées pour assurer l'intégrité des données.
* **Repositories (Couche d'accès aux données) :**  Interfaces étendant `JpaRepository` (Spring Data JPA).
    * Elles permettent d'abstraire les requêtes SQL complexes et fournissent des méthodes CRUD prêtes à l'emploi.
* **Services (Logique Métier) :** Contiennent toute la logique business de l'application.
    * Exemple : Le `InscriptionService` vérifie la disponibilité d'un créneau, le `AuthService` gère la sécurité.
* **Controllers (API & Navigation) :** Gèrent les requêtes HTTP entrantes.
    * Ils agissent comme des chefs d'orchestre : réception de la demande, appel au Service, et renvoi de la vue Thymeleaf.

### 2. Base de Données (MySQL)
L'application utilise **MySQL** pour la persistance des données en production.
* Le schéma relationnel est géré via Hibernate (ORM).
* La configuration est centralisée dans `application.properties`.

### 3. API Externe (Validation d'Adresse)
Le projet intègre une API externe via le `AddressValidationService` et `MapProxyController` pour garantir la qualité des données géographiques.
* Ce service interroge une API tiers pour vérifier et normaliser les adresses lors de l'inscription.
* Communication asynchrone via `RestTemplate` ou `WebClient`.

### 4. Frontend
* **Technologies :** HTML5, CSS3, JavaScript, Thymeleaf.
* Thymeleaf assure le rendu côté serveur et l'injection des données dynamiques.

## Fonctionnalités Clés

### Authentification et Contrôle d'Accès
L'application implémente un système de sécurité distinguant trois rôles :
* **Participants :** Consultent les ateliers et gèrent leurs propres inscriptions.
* **Animateurs :** Accèdent à des vues spécifiques pour voir leurs ateliers (en cours de développement).
* **Administrateurs :** Possèdent une vue globale sur l'événement.

Mesures de sécurité :
* **Redirection par Rôle :** Routage automatique vers le tableau de bord approprié après connexion.
* **Protection des Routes :** Des gardes (Guards) empêchent l'accès aux URLs non autorisées.
* **Isolation des Données :** Des vérifications assurent qu'un utilisateur ne peut voir que ses propres données.

### Gestion des Ateliers et Inscriptions
* **Catalogue :** Une vue catalogue permet d'explorer les ateliers disponibles.
* **Logique d'Inscription :** Inscription à des créneaux horaires précis avec pré-remplissage pour les utilisateurs connectés.
* **Workflow de Validation :** Interface administrateur pour "Accepter" ou "Refuser" les inscriptions.

## État Actuel du Développement

La logique backend et la structure de la base de données sont opérationnelles.

* **Base de données :** Schéma finalisé et fonctionnel.
* **Saisie des données :** Initialisation manuelle des ateliers.
* **Sécurité :** Stockage des mots de passe en texte clair (prototypage). L'intégration de **BCrypt** est prévue prochainement.
* **Gestion des erreurs :** Gestion basique des exceptions en place.

## Feuille de Route (Futures Features)

1. **Migration vers Angular (SPA) :** Transition de l'interface vers une Single Page Application Angular et exposition d'une API REST.
2. **Planification Dynamique :** Interface graphique complète (CRUD) pour la gestion des ateliers et créneaux par les animateurs.
3. **Gestion de Groupes :** Inscription simplifiée pour les classes ou groupes par les enseignants.
4. **Sécurité Avancée :** Chiffrement des mots de passe (BCrypt) et protection CSRF/JWT.
5. **Conteneurisation :** Mise en place de Docker et Docker Compose pour faciliter le déploiement.

## Installation

1. Cloner le dépôt.
2. Configurer la connexion MySQL dans `src/main/resources/application.properties`.
3. Compiler et lancer l'application :
    ```bash
    ./mvnw spring-boot:run
    ```
4. Accéder à l'application sur `http://localhost:8080`.

## Équipe

* **Lucas Audin**
* **Adrien Chaudron**
* **Zainab Karim**
* **Rayan Kobrossly**
* **Kassoum KONE**
* **Serigne Mbaye**
