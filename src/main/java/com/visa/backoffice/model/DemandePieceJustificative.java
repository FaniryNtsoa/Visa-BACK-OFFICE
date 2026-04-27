package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "demande_piece_justificative")
public class DemandePieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idDemandePieceJustificative;

    @Column(name = "id_demande")
    private Long idDemande;

    @Column(name = "id_piece_justificative")
    private Long idPieceJustificative;

    @Column(name = "photo_piece_justificative")
    private String photoPieceJustificative;

    @Column(name = "date_depot")
    private java.util.Date dateDepot;

    // Getters and Setters

    public Long getIdDemandePieceJustificative() {
        return idDemandePieceJustificative;
    }

    public void setIdDemandePieceJustificative(Long idDemandePieceJustificative) {
        this.idDemandePieceJustificative = idDemandePieceJustificative;
    }

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
    }

    public Long getIdPieceJustificative() {
        return idPieceJustificative;
    }

    public void setIdPieceJustificative(Long idPieceJustificative) {
        this.idPieceJustificative = idPieceJustificative;
    }

    public String getPhotoPieceJustificative() {
        return photoPieceJustificative;
    }

    public void setPhotoPieceJustificative(String photoPieceJustificative) {
        this.photoPieceJustificative = photoPieceJustificative;
    }

    public java.util.Date getDateDepot() {
        return dateDepot;
    }

    public void setDateDepot(java.util.Date dateDepot) {
        this.dateDepot = dateDepot;
    }
}