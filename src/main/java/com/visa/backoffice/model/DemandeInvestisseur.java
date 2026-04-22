package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "demande_investisseur")
public class DemandeInvestisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDemandeInvestisseur;

    @Column(name = "id_demande")
    private Long idDemande;

    @Column(name = "id_projet")
    private Long idProjet;

    @Column(name = "forme_juridique")
    private String formeJuridique;

    @Column(name = "numero_registre_commerce")
    private String numeroRegistreCommerce;

    public Long getIdDemandeInvestisseur() {
        return idDemandeInvestisseur;
    }

    public void setIdDemandeInvestisseur(Long idDemandeInvestisseur) {
        this.idDemandeInvestisseur = idDemandeInvestisseur;
    }

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
    }

    public Long getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Long idProjet) {
        this.idProjet = idProjet;
    }

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public String getNumeroRegistreCommerce() {
        return numeroRegistreCommerce;
    }

    public void setNumeroRegistreCommerce(String numeroRegistreCommerce) {
        this.numeroRegistreCommerce = numeroRegistreCommerce;
    }
}