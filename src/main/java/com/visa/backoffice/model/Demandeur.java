package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "demandeur")
public class Demandeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idDemandeur;

    private String nom;
    private String prenom;
    private java.util.Date dateNaissance;
    private String lieuNaissance;
    private String genre;
    private String adresseMada;

    @ManyToOne
    @JoinColumn(name = "id_situation_familiale")
    private SituationFamiliale idSituationFamiliale;

    @ManyToOne
    @JoinColumn(name = "id_nationalite")
    private Nationalite idNationalite;

    @ManyToOne
    @JoinColumn(name = "id_passeport")
    private Passeport idPasseport;

    // Getters and Setters

    public Long getIdDemandeur() {
        return idDemandeur;
    }

    public void setIdDemandeur(Long idDemandeur) {
        this.idDemandeur = idDemandeur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public java.util.Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(java.util.Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAdresseMada() {
        return adresseMada;
    }

    public void setAdresseMada(String adresseMada) {
        this.adresseMada = adresseMada;
    }

    public SituationFamiliale getIdSituationFamiliale() {
        return idSituationFamiliale;
    }

    public void setIdSituationFamiliale(SituationFamiliale idSituationFamiliale) {
        this.idSituationFamiliale = idSituationFamiliale;
    }

    public Nationalite getIdNationalite() {
        return idNationalite;
    }

    public void setIdNationalite(Nationalite idNationalite) {
        this.idNationalite = idNationalite;
    }

    public Passeport getIdPasseport() {
        return idPasseport;
    }

    public void setIdPasseport(Passeport idPasseport) {
        this.idPasseport = idPasseport;
    }
}