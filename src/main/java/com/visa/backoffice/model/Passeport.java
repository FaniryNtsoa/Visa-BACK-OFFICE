package com.visa.backoffice.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "passeport")
public class Passeport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasseport;

    @Column(name = "numero_passeport", nullable = false)
    private String numeroPasseport;

    @Column(name = "date_delivrance", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateDelivrance;

    @Column(name = "date_expiration", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateExpiration;

    @Column(name = "lieu_delivrance", nullable = false)
    private String lieuDelivrance;

    @Column(name = "pays_delivrance", nullable = false)
    private String paysDelivrance;

    // Getters and Setters

    public Long getIdPasseport() {
        return idPasseport;
    }

    public void setIdPasseport(Long idPasseport) {
        this.idPasseport = idPasseport;
    }

    public String getNumeroPasseport() {
        return numeroPasseport;
    }

    public void setNumeroPasseport(String numeroPasseport) {
        this.numeroPasseport = numeroPasseport;
    }

    public Date getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(Date dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getLieuDelivrance() {
        return lieuDelivrance;
    }

    public void setLieuDelivrance(String lieuDelivrance) {
        this.lieuDelivrance = lieuDelivrance;
    }

    public String getPaysDelivrance() {
        return paysDelivrance;
    }

    public void setPaysDelivrance(String paysDelivrance) {
        this.paysDelivrance = paysDelivrance;
    }
}