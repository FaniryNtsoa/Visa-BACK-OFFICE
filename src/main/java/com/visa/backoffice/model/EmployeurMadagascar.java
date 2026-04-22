package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employeur_madagascar")
public class EmployeurMadagascar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmployeur;

    @Column(name = "raison_sociale")
    private String raisonSociale;

    @Column(name = "numero_nif")
    private String numeroNif;

    @Column(name = "numero_stat")
    private String numeroStat;

    @Column(name = "secteur_activite")
    private String secteurActivite;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "nom_responsable")
    private String nomResponsable;

    @Column(name = "fonction_responsable")
    private String fonctionResponsable;

    public Long getIdEmployeur() {
        return idEmployeur;
    }

    public void setIdEmployeur(Long idEmployeur) {
        this.idEmployeur = idEmployeur;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getNumeroNif() {
        return numeroNif;
    }

    public void setNumeroNif(String numeroNif) {
        this.numeroNif = numeroNif;
    }

    public String getNumeroStat() {
        return numeroStat;
    }

    public void setNumeroStat(String numeroStat) {
        this.numeroStat = numeroStat;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getFonctionResponsable() {
        return fonctionResponsable;
    }

    public void setFonctionResponsable(String fonctionResponsable) {
        this.fonctionResponsable = fonctionResponsable;
    }
}