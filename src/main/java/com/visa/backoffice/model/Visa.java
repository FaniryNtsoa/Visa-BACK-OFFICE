package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "visa")
public class Visa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idVisa;

    @Column(name = "date_debut")
    private java.util.Date dateDebut;

    @Column(name = "date_fin")
    private java.util.Date dateFin;

    @Column(name = "numero_visa")
    private String numeroVisa;

    @ManyToOne
    @JoinColumn(name = "id_passeport")
    private Passeport passeport;

    @ManyToOne
    @JoinColumn(name = "id_demande")
    private Demande demande;

    // Getters and Setters

    public Long getIdVisa() {
        return idVisa;
    }

    public void setIdVisa(Long idVisa) {
        this.idVisa = idVisa;
    }

    public java.util.Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(java.util.Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public java.util.Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(java.util.Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getNumeroVisa() {
        return numeroVisa;
    }

    public void setNumeroVisa(String numeroVisa) {
        this.numeroVisa = numeroVisa;
    }

    public Passeport getPasseport() {
        return passeport;
    }

    public void setPasseport(Passeport passeport) {
        this.passeport = passeport;
    }

    public Demande getDemande() {
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }
}