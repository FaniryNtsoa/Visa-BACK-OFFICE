package com.visa.backoffice.service;

import com.visa.backoffice.model.*;
import com.visa.backoffice.repository.*;
import com.visa.backoffice.web.form.DuplicataForm;
import com.visa.backoffice.web.form.NouvelleDemandeForm;
import com.visa.backoffice.web.form.TransfertVisaForm;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemandeCreationService {

    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final PasseportRepository passeportRepository;
    private final NumVisaTransformableRepository numVisaTransformableRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final DemandePieceJustificativeRepository demandePieceJustificativeRepository;
    private final ProjetInvestissementRepository projetInvestissementRepository;
    private final DemandeInvestisseurRepository demandeInvestisseurRepository;
    private final EmployeurMadagascarRepository employeurMadagascarRepository;
    private final DemandeTravailleurRepository demandeTravailleurRepository;
    private final VisaRepository visaRepository;
    private final CarteResidenceRepository carteResidenceRepository;

    public DemandeCreationService(
            DemandeRepository demandeRepository,
            DemandeurRepository demandeurRepository,
            TypeVisaRepository typeVisaRepository,
            TypeDemandeRepository typeDemandeRepository,
            SituationFamilialeRepository situationFamilialeRepository,
            NationaliteRepository nationaliteRepository,
            PasseportRepository passeportRepository,
            NumVisaTransformableRepository numVisaTransformableRepository,
            PieceJustificativeRepository pieceJustificativeRepository,
            DemandePieceJustificativeRepository demandePieceJustificativeRepository,
            ProjetInvestissementRepository projetInvestissementRepository,
            DemandeInvestisseurRepository demandeInvestisseurRepository,
            EmployeurMadagascarRepository employeurMadagascarRepository,
            DemandeTravailleurRepository demandeTravailleurRepository,
            VisaRepository visaRepository,
            CarteResidenceRepository carteResidenceRepository) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.passeportRepository = passeportRepository;
        this.numVisaTransformableRepository = numVisaTransformableRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.demandePieceJustificativeRepository = demandePieceJustificativeRepository;
        this.projetInvestissementRepository = projetInvestissementRepository;
        this.demandeInvestisseurRepository = demandeInvestisseurRepository;
        this.employeurMadagascarRepository = employeurMadagascarRepository;
        this.demandeTravailleurRepository = demandeTravailleurRepository;
        this.visaRepository = visaRepository;
        this.carteResidenceRepository = carteResidenceRepository;
    }

    @Transactional
    public FormResult creerNouvelleDemande(NouvelleDemandeForm form) {
        CreationContext context = createBaseDemande(form, "Nouvelle demande", false);
        return new FormResult("Nouvelle demande enregistree.", List.of(context.demande.getIdDemande().longValue()));
    }

    @Transactional
    public FormResult creerTransfertVisa(TransfertVisaForm form) {
        Visa visa = visaRepository.findFirstByNumeroVisa(form.getNumeroVisa())
                .orElseThrow(() -> new IllegalArgumentException("Visa introuvable pour le numero fourni."));

        Passeport ancienPasseport = visa.getPasseport();
        if (ancienPasseport == null) {
            throw new IllegalArgumentException("Le visa n'a pas de passeport associe.");
        }

        Passeport nouveauPasseport = copyPasseport(ancienPasseport, form.getNumeroNouveauPasseport());
        passeportRepository.save(nouveauPasseport);

        Demande demande = createDemandeSimple(visa.getDemande(), "Transfert visa", null, null);
        Visa nouveauVisa = copyVisa(visa, nouveauPasseport, demande);
        visaRepository.save(nouveauVisa);

        Optional<CarteResidence> carteResidenceOpt = carteResidenceRepository
                .findFirstByPasseportOrderByIdCarteResidenceDesc(ancienPasseport);
        CarteResidence nouvelleCarte = copyCarteResidence(carteResidenceOpt.orElse(null), nouveauPasseport, demande, false);
        carteResidenceRepository.save(nouvelleCarte);

        return new FormResult("Transfert visa enregistre.", List.of(demande.getIdDemande().longValue()));
    }

    @Transactional
    public FormResult creerDuplicata(DuplicataForm form) {
        Visa visa = visaRepository.findFirstByNumeroVisa(form.getNumeroVisa())
                .orElseThrow(() -> new IllegalArgumentException("Visa introuvable pour le numero fourni."));

        Passeport passeport = visa.getPasseport();
        if (passeport == null) {
            throw new IllegalArgumentException("Le visa n'a pas de passeport associe.");
        }

        CarteResidence carteOrigine = carteResidenceRepository
                .findFirstByPasseportOrderByIdCarteResidenceDesc(passeport)
                .orElseThrow(() -> new IllegalArgumentException("Carte residence introuvable pour ce passeport."));

        Demande demande = createDemandeSimple(visa.getDemande(), "Duplicata", null, null);
        CarteResidence duplicata = copyCarteResidence(carteOrigine, passeport, demande, true);
        duplicata.setCarteResidenceDuplicata(carteOrigine);
        carteResidenceRepository.save(duplicata);

        return new FormResult("Duplicata enregistre.", List.of(demande.getIdDemande().longValue()));
    }

    @Transactional
    public FormResult creerTransfertVisaSansDonnees(NouvelleDemandeForm form) {
        CreationContext base = createBaseDemande(form, "Nouvelle demande", true);
        CreationContext transfert = createBaseDemande(form, "Transfert visa", false);

        Passeport nouveauPasseport = copyPasseport(base.passeport, form.getNumeroPasseport());
        passeportRepository.save(nouveauPasseport);

        Visa nouveauVisa = copyVisa(base.visa, nouveauPasseport, transfert.demande);
        visaRepository.save(nouveauVisa);

        CarteResidence nouvelleCarte = copyCarteResidence(base.carteResidence, nouveauPasseport, transfert.demande, false);
        carteResidenceRepository.save(nouvelleCarte);

        return new FormResult(
                "Transfert visa sans donnees anterieur enregistre.",
                List.of(base.demande.getIdDemande().longValue(), transfert.demande.getIdDemande().longValue()));
    }

    @Transactional
    public FormResult creerDuplicataSansDonnees(NouvelleDemandeForm form) {
        CreationContext base = createBaseDemande(form, "Nouvelle demande", true);
        CreationContext duplicata = createBaseDemande(form, "Duplicata", false);

        CarteResidence carteDuplicata = copyCarteResidence(base.carteResidence, base.passeport, duplicata.demande, true);
        carteDuplicata.setCarteResidenceDuplicata(base.carteResidence);
        carteResidenceRepository.save(carteDuplicata);

        return new FormResult(
                "Duplicata sans donnees anterieur enregistre.",
                List.of(base.demande.getIdDemande().longValue(), duplicata.demande.getIdDemande().longValue()));
    }

    private CreationContext createBaseDemande(NouvelleDemandeForm form, String typeDemande, boolean createVisaCarte) {
        Passeport passeport = buildPasseport(form);
        passeportRepository.save(passeport);

        Demandeur demandeur = buildDemandeur(form, passeport);
        demandeurRepository.save(demandeur);

        Integer visaTransformableId = createVisaTransformableIfPresent(form, demandeur);
        Demande demande = buildDemande(form, demandeur, visaTransformableId, typeDemande);
        demandeRepository.save(demande);

        TypeVisa typeVisa = null;
        if (form.getTypeVisaId() != null) {
            typeVisa = typeVisaRepository.findById(form.getTypeVisaId()).orElse(null);
        }

        if (typeVisa != null) {
            String category = typeVisa.getTypeVisa().toLowerCase(Locale.ROOT);
            if (category.contains("invest")) {
                createInvestisseurIfPresent(form, demande);
            } else if (category.contains("travail")) {
                createTravailleurIfPresent(form, demande);
            }
        }

        createPiecesJustificativesIfPresent(form, demande);

        Visa visa = null;
        CarteResidence carteResidence = null;
        if (createVisaCarte) {
            visa = createVisaFromDemande(demande, passeport);
            visaRepository.save(visa);
            carteResidence = createCarteResidenceFromVisa(demande, passeport, visa);
            carteResidenceRepository.save(carteResidence);
        }

        CreationContext context = new CreationContext();
        context.demande = demande;
        context.demandeur = demandeur;
        context.passeport = passeport;
        context.visa = visa;
        context.carteResidence = carteResidence;
        return context;
    }

    private Passeport buildPasseport(NouvelleDemandeForm form) {
        Passeport passeport = new Passeport();
        passeport.setNumeroPasseport(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeport.setLieuDelivrance(form.getLieuDelivrance());
        passeport.setPaysDelivrance(form.getPaysDelivrance());
        return passeport;
    }

    private Demandeur buildDemandeur(NouvelleDemandeForm form, Passeport passeport) {
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(form.getNom());
        demandeur.setPrenom(form.getPrenom());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setLieuNaissance(form.getLieuNaissance());
        demandeur.setGenre(form.getGenre());
        demandeur.setAdresseMada(form.getAdresseMada());
        demandeur.setIdPasseport(passeport);

        if (form.getSituationFamilialeId() != null) {
            situationFamilialeRepository.findById(form.getSituationFamilialeId())
                    .ifPresent(demandeur::setIdSituationFamiliale);
        }

        if (form.getNationaliteId() != null) {
            nationaliteRepository.findById(form.getNationaliteId())
                    .ifPresent(demandeur::setIdNationalite);
        }

        return demandeur;
    }

    private Integer createVisaTransformableIfPresent(NouvelleDemandeForm form, Demandeur demandeur) {
        if (form.getNumVisaTransformable() == null || form.getNumVisaTransformable().isBlank()) {
            return null;
        }

        NumVisaTransformable visaTransformable = new NumVisaTransformable();
        visaTransformable.setIdDemandeur(demandeur.getIdDemandeur().intValue());
        visaTransformable.setNumVisaTransformable(form.getNumVisaTransformable());
        numVisaTransformableRepository.save(visaTransformable);
        return visaTransformable.getIdVisaTransformable();
    }

    private Demande buildDemande(NouvelleDemandeForm form, Demandeur demandeur, Integer visaTransformableId, String typeDemande) {
        Demande demande = new Demande();
        demande.setDateDemande(new Date());
        demande.setIdDemandeur(demandeur.getIdDemandeur().intValue());

        if (visaTransformableId != null) {
            demande.setIdVisaTransformable(visaTransformableId);
        }

        if (form.getTypeVisaId() != null) {
            demande.setIdTypeVisa(form.getTypeVisaId().intValue());
        }

        if (typeDemande != null) {
            typeDemandeRepository.findFirstByTypeDemandeIgnoreCase(typeDemande)
                    .ifPresent(td -> demande.setIdTypeDemande(td.getIdTypeDemande()));
        }

        return demande;
    }

    private Demande createDemandeSimple(Demande baseDemande, String typeDemande, Integer typeVisaId, Integer demandeurId) {
        Demande demande = new Demande();
        demande.setDateDemande(new Date());

        if (demandeurId != null) {
            demande.setIdDemandeur(demandeurId);
        } else if (baseDemande != null && baseDemande.getIdDemandeur() != null) {
            demande.setIdDemandeur(baseDemande.getIdDemandeur());
        }

        if (typeVisaId != null) {
            demande.setIdTypeVisa(typeVisaId);
        } else if (baseDemande != null && baseDemande.getIdTypeVisa() != null) {
            demande.setIdTypeVisa(baseDemande.getIdTypeVisa());
        }

        if (typeDemande != null) {
            typeDemandeRepository.findFirstByTypeDemandeIgnoreCase(typeDemande)
                    .ifPresent(td -> demande.setIdTypeDemande(td.getIdTypeDemande()));
        }

        return demandeRepository.save(demande);
    }

    private void createPiecesJustificativesIfPresent(NouvelleDemandeForm form, Demande demande) {
        if (form.getPiecesJustificatives() == null || form.getPiecesJustificatives().isEmpty()) {
            return;
        }

        for (Long pieceId : form.getPiecesJustificatives()) {
            PieceJustificative piece = pieceJustificativeRepository.findById(pieceId).orElse(null);
            if (piece == null) {
                continue;
            }

            DemandePieceJustificative link = new DemandePieceJustificative();
            link.setIdDemande(demande.getIdDemande().longValue());
            link.setIdPieceJustificative(piece.getIdPieceJustificative());
            link.setDateDepot(new Date());
            demandePieceJustificativeRepository.save(link);
        }
    }

    private void createInvestisseurIfPresent(NouvelleDemandeForm form, Demande demande) {
        if (!hasInvestisseurData(form)) {
            return;
        }

        ProjetInvestissement projet = new ProjetInvestissement();
        projet.setNomProjet(form.getNomProjet());
        projet.setSecteur(form.getSecteur());
        projet.setDescriptionProjet(form.getDescriptionProjet());
        projet.setMontantInvestissement(form.getMontantInvestissement());
        projet.setDevise(form.getDevise());
        projet.setZoneInvestissement(form.getZoneInvestissement());
        projet.setNombreEmploisCrees(form.getNombreEmploisCrees());
        projet.setDureeProjetMois(form.getDureeProjetMois());
        projetInvestissementRepository.save(projet);

        DemandeInvestisseur demandeInvestisseur = new DemandeInvestisseur();
        demandeInvestisseur.setIdDemande(demande.getIdDemande().longValue());
        demandeInvestisseur.setIdProjet(projet.getIdProjet());
        demandeInvestisseur.setFormeJuridique(form.getFormeJuridique());
        demandeInvestisseur.setNumeroRegistreCommerce(form.getNumeroRegistreCommerce());
        demandeInvestisseurRepository.save(demandeInvestisseur);
    }

    private void createTravailleurIfPresent(NouvelleDemandeForm form, Demande demande) {
        if (!hasTravailleurData(form)) {
            return;
        }

        EmployeurMadagascar employeur = new EmployeurMadagascar();
        employeur.setRaisonSociale(form.getRaisonSociale());
        employeur.setNumeroNif(form.getNumeroNif());
        employeur.setNumeroStat(form.getNumeroStat());
        employeur.setSecteurActivite(form.getSecteurActivite());
        employeur.setAdresse(form.getAdresseEmployeur());
        employeur.setTelephone(form.getTelephoneEmployeur());
        employeur.setEmail(form.getEmailEmployeur());
        employeur.setNomResponsable(form.getNomResponsable());
        employeur.setFonctionResponsable(form.getFonctionResponsable());
        employeurMadagascarRepository.save(employeur);

        DemandeTravailleur demandeTravailleur = new DemandeTravailleur();
        demandeTravailleur.setIdDemande(demande.getIdDemande().longValue());
        demandeTravailleur.setIdEmployeur(employeur.getIdEmployeur());
        demandeTravailleur.setPosteOccupe(form.getPosteOccupe());
        demandeTravailleur.setTypeContrat(form.getTypeContrat());
        demandeTravailleur.setDureeContratMois(form.getDureeContratMois());
        demandeTravailleur.setSalaireMensuel(form.getSalaireMensuel());
        demandeTravailleur.setDeviseSalaire(form.getDeviseSalaire());
        demandeTravailleurRepository.save(demandeTravailleur);
    }

    private boolean hasInvestisseurData(NouvelleDemandeForm form) {
        return hasText(form.getNomProjet())
                || hasText(form.getSecteur())
                || hasText(form.getDescriptionProjet())
                || form.getMontantInvestissement() != null
                || hasText(form.getDevise())
                || hasText(form.getZoneInvestissement())
                || form.getNombreEmploisCrees() != null
                || form.getDureeProjetMois() != null
                || hasText(form.getFormeJuridique())
                || hasText(form.getNumeroRegistreCommerce());
    }

    private boolean hasTravailleurData(NouvelleDemandeForm form) {
        return hasText(form.getRaisonSociale())
                || hasText(form.getNumeroNif())
                || hasText(form.getNumeroStat())
                || hasText(form.getSecteurActivite())
                || hasText(form.getAdresseEmployeur())
                || hasText(form.getTelephoneEmployeur())
                || hasText(form.getEmailEmployeur())
                || hasText(form.getNomResponsable())
                || hasText(form.getFonctionResponsable())
                || hasText(form.getPosteOccupe())
                || hasText(form.getTypeContrat())
                || form.getDureeContratMois() != null
                || form.getSalaireMensuel() != null
                || hasText(form.getDeviseSalaire());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Passeport copyPasseport(Passeport source, String nouveauNumero) {
        Passeport passeport = new Passeport();
        passeport.setNumeroPasseport(nouveauNumero);
        passeport.setDateDelivrance(source.getDateDelivrance());
        passeport.setDateExpiration(source.getDateExpiration());
        passeport.setLieuDelivrance(source.getLieuDelivrance());
        passeport.setPaysDelivrance(source.getPaysDelivrance());
        return passeport;
    }

    private Visa copyVisa(Visa source, Passeport passeport, Demande demande) {
        Visa visa = new Visa();
        if (source != null) {
            visa.setNumeroVisa(source.getNumeroVisa());
            visa.setDateDebut(source.getDateDebut());
            visa.setDateFin(source.getDateFin());
        }
        visa.setPasseport(passeport);
        visa.setDemande(demande);
        return visa;
    }

    private CarteResidence copyCarteResidence(CarteResidence source, Passeport passeport, Demande demande, boolean duplicata) {
        CarteResidence carteResidence = new CarteResidence();
        if (source != null) {
            carteResidence.setNumeroCarte(source.getNumeroCarte());
            carteResidence.setDateDebut(source.getDateDebut());
            carteResidence.setDateFin(source.getDateFin());
        }
        carteResidence.setIsDuplicata(duplicata);
        carteResidence.setPasseport(passeport);
        carteResidence.setDemande(demande);
        return carteResidence;
    }

    private Visa createVisaFromDemande(Demande demande, Passeport passeport) {
        Visa visa = new Visa();
        visa.setPasseport(passeport);
        visa.setDemande(demande);
        return visa;
    }

    private CarteResidence createCarteResidenceFromVisa(Demande demande, Passeport passeport, Visa visa) {
        CarteResidence carteResidence = new CarteResidence();
        carteResidence.setPasseport(passeport);
        carteResidence.setDemande(demande);
        carteResidence.setIsDuplicata(false);
        return carteResidence;
    }

    private static class CreationContext {
        private Demande demande;
        private Demandeur demandeur;
        private Passeport passeport;
        private Visa visa;
        private CarteResidence carteResidence;
    }
}
