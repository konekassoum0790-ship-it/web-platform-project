package com.example.fetescience.dto;

/**
 * DTO pour transférer les informations d'un créneau avec disponibilité
 */
public class CreneauDTO {
    private Long id;
    private int horaireDebut;  // ✅ Changé en int pour correspondre à votre modèle
    private Integer duree;
    private String lieu;
    private Integer capacite;
    private Integer inscriptionsCount;
    private Integer placesRestantes;
    private boolean complet;

    // Constructeur adapté à votre modèle Creneau
    public CreneauDTO(Long id, int horaireDebut, Integer duree,
                      String lieu, Integer capacite, Integer inscriptionsCount) {
        this.id = id;
        this.horaireDebut = horaireDebut;
        this.duree = duree;
        this.lieu = lieu;
        this.capacite = capacite;
        this.inscriptionsCount = inscriptionsCount;
        this.placesRestantes = capacite - inscriptionsCount;
        this.complet = this.placesRestantes <= 0;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHoraireDebut() {
        return horaireDebut;
    }

    public void setHoraireDebut(int horaireDebut) {
        this.horaireDebut = horaireDebut;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Integer getInscriptionsCount() {
        return inscriptionsCount;
    }

    public void setInscriptionsCount(Integer inscriptionsCount) {
        this.inscriptionsCount = inscriptionsCount;
    }

    public Integer getPlacesRestantes() {
        return placesRestantes;
    }

    public void setPlacesRestantes(Integer placesRestantes) {
        this.placesRestantes = placesRestantes;
    }

    public boolean isComplet() {
        return complet;
    }

    public void setComplet(boolean complet) {
        this.complet = complet;
    }
}