package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "piece_justificative")
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idPieceJustificative;

    @Column(name = "piece_justificative", nullable = false)
    private String pieceJustificative;

    @Column(name = "description")
    private String description;

    public Long getIdPieceJustificative() {
        return idPieceJustificative;
    }

    public void setIdPieceJustificative(Long idPieceJustificative) {
        this.idPieceJustificative = idPieceJustificative;
    }

    public String getPieceJustificative() {
        return pieceJustificative;
    }

    public void setPieceJustificative(String pieceJustificative) {
        this.pieceJustificative = pieceJustificative;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}