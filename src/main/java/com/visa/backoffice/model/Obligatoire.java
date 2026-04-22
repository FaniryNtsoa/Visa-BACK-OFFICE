package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "obligatoire")
public class Obligatoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idObligatoire;

    @ManyToOne
    @JoinColumn(name = "id_type_visa", nullable = false)
    private TypeVisa idTypeVisa;

    @Column(name = "nom_colonne_obligatoire", nullable = false)
    private String nomColonneObligatoire;

    @Column(name = "nom_table", nullable = false)
    private String nomTable;

    public Long getIdObligatoire() {
        return idObligatoire;
    }

    public void setIdObligatoire(Long idObligatoire) {
        this.idObligatoire = idObligatoire;
    }

    public TypeVisa getIdTypeVisa() {
        return idTypeVisa;
    }

    public void setIdTypeVisa(TypeVisa idTypeVisa) {
        this.idTypeVisa = idTypeVisa;
    }

    public String getNomColonneObligatoire() {
        return nomColonneObligatoire;
    }

    public void setNomColonneObligatoire(String nomColonneObligatoire) {
        this.nomColonneObligatoire = nomColonneObligatoire;
    }

    public String getNomTable() {
        return nomTable;
    }

    public void setNomTable(String nomTable) {
        this.nomTable = nomTable;
    }
}