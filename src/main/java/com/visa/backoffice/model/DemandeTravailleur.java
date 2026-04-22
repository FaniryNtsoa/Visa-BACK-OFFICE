package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "demande_travailleur")
public class DemandeTravailleur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande_travailleur")
    private Long idDemandeTravailleur;

    @Column(name = "id_demande")
    private Long idDemande;

    @Column(name = "id_employeur")
    private Long idEmployeur;

    @Column(name = "poste_occupe")
    private String posteOccupe;

    @Column(name = "type_contrat")
    private String typeContrat;

    @Column(name = "duree_contrat_mois")
    private Integer dureeContratMois;

    @Column(name = "salaire_mensuel")
    private Double salaireMensuel;

    @Column(name = "devise_salaire")
    private String deviseSalaire;

    // Getters and Setters

    public Long getIdDemandeTravailleur() {
        return idDemandeTravailleur;
    }

    public void setIdDemandeTravailleur(Long idDemandeTravailleur) {
        this.idDemandeTravailleur = idDemandeTravailleur;
    }

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
    }

    public Long getIdEmployeur() {
        return idEmployeur;
    }

    public void setIdEmployeur(Long idEmployeur) {
        this.idEmployeur = idEmployeur;
    }

    public String getPosteOccupe() {
        return posteOccupe;
    }

    public void setPosteOccupe(String posteOccupe) {
        this.posteOccupe = posteOccupe;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public Integer getDureeContratMois() {
        return dureeContratMois;
    }

    public void setDureeContratMois(Integer dureeContratMois) {
        this.dureeContratMois = dureeContratMois;
    }

    public Double getSalaireMensuel() {
        return salaireMensuel;
    }

    public void setSalaireMensuel(Double salaireMensuel) {
        this.salaireMensuel = salaireMensuel;
    }

    public String getDeviseSalaire() {
        return deviseSalaire;
    }

    public void setDeviseSalaire(String deviseSalaire) {
        this.deviseSalaire = deviseSalaire;
    }
}