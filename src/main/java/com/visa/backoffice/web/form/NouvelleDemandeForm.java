package com.visa.backoffice.web.form;

import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class NouvelleDemandeForm {

    // Etat civil
    private String nom;
    private String prenom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date dateNaissance;

    private String lieuNaissance;
    private String genre;
    private String adresseMada;
    private Long situationFamilialeId;
    private Integer nationaliteId;

    // Passeport
    private String numeroPasseport;
    private String numeroAncienPasseport;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date dateDelivrance;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date dateExpiration;

    private String lieuDelivrance;
    private String paysDelivrance;

    // Visa
    private Long typeVisaId;
    private String numVisaTransformable;

    // Pieces justificatives
    private List<Long> piecesJustificatives;

    // Investisseur
    private String nomProjet;
    private String secteur;
    private String descriptionProjet;
    private Double montantInvestissement;
    private String devise;
    private String zoneInvestissement;
    private Integer nombreEmploisCrees;
    private Integer dureeProjetMois;
    private String formeJuridique;
    private String numeroRegistreCommerce;

    // Travailleur
    private String raisonSociale;
    private String numeroNif;
    private String numeroStat;
    private String secteurActivite;
    private String adresseEmployeur;
    private String telephoneEmployeur;
    private String emailEmployeur;
    private String nomResponsable;
    private String fonctionResponsable;

    private String posteOccupe;
    private String typeContrat;
    private Integer dureeContratMois;
    private Double salaireMensuel;
    private String deviseSalaire;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public java.util.Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(java.util.Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAdresseMada() {
        return adresseMada;
    }

    public void setAdresseMada(String adresseMada) {
        this.adresseMada = adresseMada;
    }

    public Long getSituationFamilialeId() {
        return situationFamilialeId;
    }

    public void setSituationFamilialeId(Long situationFamilialeId) {
        this.situationFamilialeId = situationFamilialeId;
    }

    public Integer getNationaliteId() {
        return nationaliteId;
    }

    public void setNationaliteId(Integer nationaliteId) {
        this.nationaliteId = nationaliteId;
    }

    public String getNumeroPasseport() {
        return numeroPasseport;
    }

    public void setNumeroPasseport(String numeroPasseport) {
        this.numeroPasseport = numeroPasseport;
    }

    public String getNumeroAncienPasseport() {
        return numeroAncienPasseport;
    }

    public void setNumeroAncienPasseport(String numeroAncienPasseport) {
        this.numeroAncienPasseport = numeroAncienPasseport;
    }

    public java.util.Date getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(java.util.Date dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
    }

    public java.util.Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(java.util.Date dateExpiration) {
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

    public Long getTypeVisaId() {
        return typeVisaId;
    }

    public void setTypeVisaId(Long typeVisaId) {
        this.typeVisaId = typeVisaId;
    }

    public String getNumVisaTransformable() {
        return numVisaTransformable;
    }

    public void setNumVisaTransformable(String numVisaTransformable) {
        this.numVisaTransformable = numVisaTransformable;
    }

    public List<Long> getPiecesJustificatives() {
        return piecesJustificatives;
    }

    public void setPiecesJustificatives(List<Long> piecesJustificatives) {
        this.piecesJustificatives = piecesJustificatives;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public String getDescriptionProjet() {
        return descriptionProjet;
    }

    public void setDescriptionProjet(String descriptionProjet) {
        this.descriptionProjet = descriptionProjet;
    }

    public Double getMontantInvestissement() {
        return montantInvestissement;
    }

    public void setMontantInvestissement(Double montantInvestissement) {
        this.montantInvestissement = montantInvestissement;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getZoneInvestissement() {
        return zoneInvestissement;
    }

    public void setZoneInvestissement(String zoneInvestissement) {
        this.zoneInvestissement = zoneInvestissement;
    }

    public Integer getNombreEmploisCrees() {
        return nombreEmploisCrees;
    }

    public void setNombreEmploisCrees(Integer nombreEmploisCrees) {
        this.nombreEmploisCrees = nombreEmploisCrees;
    }

    public Integer getDureeProjetMois() {
        return dureeProjetMois;
    }

    public void setDureeProjetMois(Integer dureeProjetMois) {
        this.dureeProjetMois = dureeProjetMois;
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

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getNumeroNif() {
        return numeroNif;
    }

    public void setNumeroNif(String numeroNif) {
        this.numeroNif = numeroNif;
    }

    public String getNumeroStat() {
        return numeroStat;
    }

    public void setNumeroStat(String numeroStat) {
        this.numeroStat = numeroStat;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
    }

    public String getAdresseEmployeur() {
        return adresseEmployeur;
    }

    public void setAdresseEmployeur(String adresseEmployeur) {
        this.adresseEmployeur = adresseEmployeur;
    }

    public String getTelephoneEmployeur() {
        return telephoneEmployeur;
    }

    public void setTelephoneEmployeur(String telephoneEmployeur) {
        this.telephoneEmployeur = telephoneEmployeur;
    }

    public String getEmailEmployeur() {
        return emailEmployeur;
    }

    public void setEmailEmployeur(String emailEmployeur) {
        this.emailEmployeur = emailEmployeur;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getFonctionResponsable() {
        return fonctionResponsable;
    }

    public void setFonctionResponsable(String fonctionResponsable) {
        this.fonctionResponsable = fonctionResponsable;
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
