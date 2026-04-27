package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "projet_investissement")
public class ProjetInvestissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idProjet;

    private String nomProjet;
    private String secteur;
    private String descriptionProjet;
    private Double montantInvestissement;
    private String devise;
    private String zoneInvestissement;
    private Integer nombreEmploisCrees;
    private Integer dureeProjetMois;

    // Getters and Setters

    public Long getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Long idProjet) {
        this.idProjet = idProjet;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public String getDescriptionProjet() {
        return descriptionProjet;
    }

    public void setDescriptionProjet(String descriptionProjet) {
        this.descriptionProjet = descriptionProjet;
    }

    public Double getMontantInvestissement() {
        return montantInvestissement;
    }

    public void setMontantInvestissement(Double montantInvestissement) {
        this.montantInvestissement = montantInvestissement;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getZoneInvestissement() {
        return zoneInvestissement;
    }

    public void setZoneInvestissement(String zoneInvestissement) {
        this.zoneInvestissement = zoneInvestissement;
    }

    public Integer getNombreEmploisCrees() {
        return nombreEmploisCrees;
    }

    public void setNombreEmploisCrees(Integer nombreEmploisCrees) {
        this.nombreEmploisCrees = nombreEmploisCrees;
    }

    public Integer getDureeProjetMois() {
        return dureeProjetMois;
    }

    public void setDureeProjetMois(Integer dureeProjetMois) {
        this.dureeProjetMois = dureeProjetMois;
    }
}