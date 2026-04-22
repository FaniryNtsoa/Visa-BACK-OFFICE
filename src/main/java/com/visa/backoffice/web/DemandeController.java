package com.visa.backoffice.web;

import com.visa.backoffice.model.*;
import com.visa.backoffice.repository.*;
import com.visa.backoffice.web.form.NouvelleDemandeForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository     passeportRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final DemandePieceJustificativeRepository demandePieceJustificativeRepository;

    public DemandeController(DemandeRepository demandeRepository, 
                             DemandeurRepository demandeurRepository,
                             PasseportRepository passeportRepository,
                             TypeVisaRepository typeVisaRepository,
                             SituationFamilialeRepository situationFamilialeRepository,
                             NationaliteRepository nationaliteRepository,
                             PieceJustificativeRepository pieceJustificativeRepository,
                             DemandePieceJustificativeRepository demandePieceJustificativeRepository) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.demandePieceJustificativeRepository = demandePieceJustificativeRepository;
    }

    @GetMapping("/liste")
    public String listerDemandes(Model model) {
        model.addAttribute("demandes", demandeRepository.findAll());
        return "lists/demande-liste";
    }

    @GetMapping("/modifier/{id}")
    public String afficherFormulaireModification(@PathVariable("id") Long id, Model model) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Demandeur introuvable"));
        
        Passeport passeport = demandeur.getIdPasseport();

        NouvelleDemandeForm form = new NouvelleDemandeForm();
        // Mapping Demandeur
        form.setNom(demandeur.getNom());
        form.setPrenom(demandeur.getPrenom());
        form.setDateNaissance(demandeur.getDateNaissance());
        form.setLieuNaissance(demandeur.getLieuNaissance());
        form.setGenre(demandeur.getGenre());
        form.setAdresseMada(demandeur.getAdresseMada());
        form.setSituationFamilialeId(demandeur.getIdSituationFamiliale().getIdSituationFamiliale());
        form.setNationaliteId(demandeur.getIdNationalite().getIdNationalite());

        // Mapping Passeport
        form.setNumeroPasseport(passeport.getNumeroPasseport());
        form.setDateDelivrance(passeport.getDateDelivrance());
        form.setDateExpiration(passeport.getDateExpiration());
        form.setLieuDelivrance(passeport.getLieuDelivrance());
        form.setPaysDelivrance(passeport.getPaysDelivrance());

        // Mapping Demande
        form.setTypeVisaId(Long.valueOf(demande.getIdTypeVisa()));

        // Pièces déjà cochées
        List<Long> checkedPieces = demandePieceJustificativeRepository.findAll()
                .stream()
                .filter(p -> p.getIdDemande().equals(id))
                .map(DemandePieceJustificative::getIdPieceJustificative)
                .map(Long::valueOf)
                .toList();

        model.addAttribute("form", form);
        model.addAttribute("demandeId", id);
        model.addAttribute("checkedPieces", checkedPieces);
        model.addAttribute("situations", situationFamilialeRepository.findAll());
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("typeVisas", typeVisaRepository.findAll());
        model.addAttribute("pieces", pieceJustificativeRepository.findAll());

        return "modifs/demande-modifier";
    }

    @PostMapping("/modifier/{id}")
    public String modifierDemande(@PathVariable("id") Long id, @ModelAttribute("form") NouvelleDemandeForm form, @RequestParam(value = "piecesJustificatives", required = false) List<Long> pieces) {
        Demande demande = demandeRepository.findById(id.intValue()).get();
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue()).get();
        Passeport passeport = demandeur.getIdPasseport();

        // Mise à jour Passeport
        passeport.setNumeroPasseport(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeport.setLieuDelivrance(form.getLieuDelivrance());
        passeport.setPaysDelivrance(form.getPaysDelivrance());
        passeportRepository.save(passeport);

        // Mise à jour Demandeur
        demandeur.setNom(form.getNom());
        demandeur.setPrenom(form.getPrenom());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setLieuNaissance(form.getLieuNaissance());
        demandeur.setGenre(form.getGenre());
        demandeur.setAdresseMada(form.getAdresseMada());
        demandeurRepository.save(demandeur);

        // Ajout des nouvelles pièces justificatives uniquement
        if (pieces != null) {
            for (Long pieceId : pieces) {
                boolean existeDeja = demandePieceJustificativeRepository.findAll().stream()
                        .anyMatch(p -> p.getIdDemande().equals(id) && p.getIdPieceJustificative().equals(pieceId.intValue()));
                
                if (!existeDeja) {
                    DemandePieceJustificative dpj = new DemandePieceJustificative();
                    dpj.setIdDemande(id);
                    dpj.setIdPieceJustificative(pieceId.longValue());
                    dpj.setDateDepot(new java.util.Date());
                    demandePieceJustificativeRepository.save(dpj);
                }
            }
        }

        return "redirect:/demandes/liste";
    }
}