package com.visa.backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "type_visa")
public class TypeVisa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_visa")
    private Long idTypeVisa;

    @Column(name = "type_visa", nullable = false)
    private String typeVisa;

    @Column(name = "duree_validite_mois")
    private Integer dureeValiditeMois;

    @Column(name = "description")
    private String description;

    public Long getIdTypeVisa() {
        return idTypeVisa;
    }

    public void setIdTypeVisa(Long idTypeVisa) {
        this.idTypeVisa = idTypeVisa;
    }

    public String getTypeVisa() {
        return typeVisa;
    }

    public void setTypeVisa(String typeVisa) {
        this.typeVisa = typeVisa;
    }

    public Integer getDureeValiditeMois() {
        return dureeValiditeMois;
    }

    public void setDureeValiditeMois(Integer dureeValiditeMois) {
        this.dureeValiditeMois = dureeValiditeMois;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}