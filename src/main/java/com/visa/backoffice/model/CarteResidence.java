package com.visa.backoffice.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class CarteResidence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarteResidence;

    private String numeroCarte;
    private Date dateDebut;
    private Date dateFin;
    private Boolean isDuplicata;

    @ManyToOne
    @JoinColumn(name = "id_carte_residence_duplicata")
    private CarteResidence carteResidenceDuplicata;

    @ManyToOne
    @JoinColumn(name = "id_passeport")
    private Passeport passeport;

    @ManyToOne
    @JoinColumn(name = "id_demande")
    private Demande demande;

    // Getters and Setters

    public Long getIdCarteResidence() {
        return idCarteResidence;
    }

    public void setIdCarteResidence(Long idCarteResidence) {
        this.idCarteResidence = idCarteResidence;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Boolean getIsDuplicata() {
        return isDuplicata;
    }

    public void setIsDuplicata(Boolean isDuplicata) {
        this.isDuplicata = isDuplicata;
    }

    public CarteResidence getCarteResidenceDuplicata() {
        return carteResidenceDuplicata;
    }

    public void setCarteResidenceDuplicata(CarteResidence carteResidenceDuplicata) {
        this.carteResidenceDuplicata = carteResidenceDuplicata;
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