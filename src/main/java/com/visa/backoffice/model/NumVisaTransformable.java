package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "num_visa_transformable")
public class NumVisaTransformable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idVisaTransformable;

    @Column(name = "id_demandeur")
    private Integer idDemandeur;

    @Column(name = "num_visa_transformable")
    private String numVisaTransformable;

    @Column(name = "date_expiration_visa")
    private java.util.Date dateExpirationVisa;

    // Getters and Setters

    public Integer getIdVisaTransformable() {
        return idVisaTransformable;
    }

    public void setIdVisaTransformable(Integer idVisaTransformable) {
        this.idVisaTransformable = idVisaTransformable;
    }

    public Integer getIdDemandeur() {
        return idDemandeur;
    }

    public void setIdDemandeur(Integer idDemandeur) {
        this.idDemandeur = idDemandeur;
    }

    public String getNumVisaTransformable() {
        return numVisaTransformable;
    }

    public void setNumVisaTransformable(String numVisaTransformable) {
        this.numVisaTransformable = numVisaTransformable;
    }

    public java.util.Date getDateExpirationVisa() {
        return dateExpirationVisa;
    }

    public void setDateExpirationVisa(java.util.Date dateExpirationVisa) {
        this.dateExpirationVisa = dateExpirationVisa;
    }
}