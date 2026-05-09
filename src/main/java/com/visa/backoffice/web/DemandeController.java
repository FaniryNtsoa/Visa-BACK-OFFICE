package com.visa.backoffice.web;

import com.visa.backoffice.model.*;
import com.visa.backoffice.repository.*;
import com.visa.backoffice.web.form.NouvelleDemandeForm;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository     passeportRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final DemandePieceJustificativeRepository demandePieceJustificativeRepository;
    private final DemandeTravailleurRepository demandeTravailleurRepository;
    private final EmployeurMadagascarRepository employeurMadagascarRepository;
    private final DemandeInvestisseurRepository demandeInvestisseurRepository;
    private final ProjetInvestissementRepository projetInvestissementRepository;
    private final NumVisaTransformableRepository numVisaTransformableRepository;
    private final StatusDemandeRepository statusDemandeRepository;
    private final DemandeStatusHistoryRepository demandeStatusHistoryRepository;
    private final Path uploadRoot = Paths.get("uploads", "demandes");

    public DemandeController(DemandeRepository demandeRepository, 
                             DemandeurRepository demandeurRepository,
                             PasseportRepository passeportRepository,
                             TypeVisaRepository typeVisaRepository,
                             SituationFamilialeRepository situationFamilialeRepository,
                             NationaliteRepository nationaliteRepository,
                             PieceJustificativeRepository pieceJustificativeRepository,
                             DemandePieceJustificativeRepository demandePieceJustificativeRepository,
                             DemandeTravailleurRepository demandeTravailleurRepository,
                             EmployeurMadagascarRepository employeurMadagascarRepository,
                             DemandeInvestisseurRepository demandeInvestisseurRepository,
                             ProjetInvestissementRepository projetInvestissementRepository,
                             NumVisaTransformableRepository numVisaTransformableRepository,
                             TypeDemandeRepository typeDemandeRepository,
                             StatusDemandeRepository statusDemandeRepository,
                             DemandeStatusHistoryRepository demandeStatusHistoryRepository) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.demandePieceJustificativeRepository = demandePieceJustificativeRepository;
        this.demandeTravailleurRepository = demandeTravailleurRepository;
        this.employeurMadagascarRepository = employeurMadagascarRepository;
        this.demandeInvestisseurRepository = demandeInvestisseurRepository;
        this.projetInvestissementRepository = projetInvestissementRepository;
        this.numVisaTransformableRepository = numVisaTransformableRepository;
        this.statusDemandeRepository = statusDemandeRepository;
        this.demandeStatusHistoryRepository = demandeStatusHistoryRepository;
    }

    @GetMapping("/liste")
    public String listerDemandes(Model model) {
        List<Demande> demandes = demandeRepository.findAll();
        Map<Integer, Demandeur> demandeurs = demandeurRepository
            .findAllById(extractIdsLong(demandes, Demande::getIdDemandeur))
            .stream()
            .collect(Collectors.toMap(d -> d.getIdDemandeur().intValue(), Function.identity()));
        Map<Integer, TypeDemande> typeDemandes = typeDemandeRepository
            .findAllById(extractIdsInt(demandes, Demande::getIdTypeDemande))
            .stream()
            .collect(Collectors.toMap(td -> td.getIdTypeDemande().intValue(), Function.identity()));
        Map<Integer, TypeVisa> typeVisas = typeVisaRepository
            .findAllById(extractIdsLong(demandes, Demande::getIdTypeVisa))
            .stream()
            .collect(Collectors.toMap(v -> v.getIdTypeVisa().intValue(), Function.identity()));
        Map<Integer, StatusDemande> statuts = statusDemandeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(StatusDemande::getIdStatus, Function.identity()));
        Map<Integer, String> latestStatusLabelByDemande = buildLatestStatusLabelByDemande(demandes, statuts);
        Map<Integer, Boolean> piecesCompletesByDemande = buildPiecesCompletesByDemande(demandes);
        Map<Integer, Boolean> photoSignatureCompletesByDemande = buildPhotoSignatureCompletesByDemande(demandes, demandeurs);
        Map<Integer, Boolean> scanTermineByDemande = buildScanTermineByDemande(demandes, statuts);
        Map<Integer, Boolean> visaApprouveByDemande = buildVisaApprouveByDemande(demandes, statuts);

        model.addAttribute("demandes", demandes);
        model.addAttribute("demandeurs", demandeurs);
        model.addAttribute("typeDemandes", typeDemandes);
        model.addAttribute("typeVisas", typeVisas);
        model.addAttribute("latestStatusLabelByDemande", latestStatusLabelByDemande);
        model.addAttribute("piecesCompletesByDemande", piecesCompletesByDemande);
        model.addAttribute("photoSignatureCompletesByDemande", photoSignatureCompletesByDemande);
        model.addAttribute("scanTermineByDemande", scanTermineByDemande);
        model.addAttribute("visaApprouveByDemande", visaApprouveByDemande);
        return "lists/demande-liste";
    }

    @GetMapping("/{id}/uploader")
    public String afficherUploader(@PathVariable("id") Long id, Model model) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));

        List<DemandePieceJustificative> demandesPieces = dedupePieces(demandePieceJustificativeRepository.findByIdDemande(id));
        Set<Long> pieceIds = demandesPieces.stream()
            .map(DemandePieceJustificative::getIdPieceJustificative)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, PieceJustificative> piecesById = pieceJustificativeRepository.findAllById(pieceIds)
                .stream()
                .collect(Collectors.toMap(PieceJustificative::getIdPieceJustificative, Function.identity()));

        Map<Long, List<UploadedFileView>> fichiersParPiece = new HashMap<>();
        for (DemandePieceJustificative dpj : demandesPieces) {
            List<String> files = parseUploadedFiles(dpj.getPhotoPieceJustificative());
            List<UploadedFileView> views = files.stream()
                .map(file -> new UploadedFileView(
                    file,
                    "/demandes/" + id + "/pieces/" + dpj.getIdPieceJustificative() + "/files/" + file,
                    isImageFile(file)))
                .toList();
            fichiersParPiece.put(dpj.getIdPieceJustificative(), views);
        }

        model.addAttribute("demande", demande);
        model.addAttribute("demandesPieces", demandesPieces);
        model.addAttribute("piecesById", piecesById);
        model.addAttribute("fichiersParPiece", fichiersParPiece);
        model.addAttribute("allPiecesUploaded", areAllPiecesUploaded(demandesPieces));
        return "forms/demande-uploader";
    }

    @GetMapping("/{id}/pieces/{pieceId}/files/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> lireFichierPiece(
            @PathVariable("id") Long id,
            @PathVariable("pieceId") Long pieceId,
            @PathVariable("fileName") String fileName) throws IOException {
        Path pieceDir = uploadRoot.resolve(String.valueOf(id)).resolve(String.valueOf(pieceId)).normalize();
        Path filePath = pieceDir.resolve(Paths.get(fileName).getFileName().toString()).normalize();

        if (!filePath.startsWith(pieceDir) || !Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        MediaType mediaType = contentType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/{id}/pieces/{pieceId}/upload")
    public String uploaderPiece(
            @PathVariable("id") Long id,
            @PathVariable("pieceId") Long pieceId,
            @RequestParam("files") MultipartFile[] files,
            RedirectAttributes redirectAttributes) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        if (isScanTermine(demande)) {
            throw new IllegalArgumentException("Upload impossible: scan deja termine pour cette demande.");
        }

        DemandePieceJustificative dpj = demandePieceJustificativeRepository
                .findFirstByIdDemandeAndIdPieceJustificative(id, pieceId)
                .orElseThrow(() -> new IllegalArgumentException("Piece justificative introuvable pour cette demande."));

        if (files == null || files.length == 0 || isAllEmpty(files)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Veuillez selectionner au moins un fichier.");
            return "redirect:/demandes/" + id + "/uploader";
        }

        try {
            List<String> savedFiles = replacePieceFiles(id, pieceId, files);
            dpj.setPhotoPieceJustificative(String.join(";", savedFiles));
            dpj.setDateDepot(new Date());
            demandePieceJustificativeRepository.save(dpj);
            redirectAttributes.addFlashAttribute("successMessage", "Fichiers uploades avec succes.");
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'upload des fichiers.");
        }

        return "redirect:/demandes/" + id + "/uploader";
    }

    @PostMapping("/{id}/scan-terminer")
    public String marquerScanTerminer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));

        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
                .orElse(null);
        if (demandeur == null || !hasPhotoAndSignature(demandeur)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Photo et signature du demandeur obligatoires avant le scan termine.");
            return "redirect:/demandes/liste";
        }

        List<DemandePieceJustificative> pieces = demandePieceJustificativeRepository.findByIdDemande(id);
        if (!areAllPiecesUploaded(pieces)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Toutes les pieces justificatives doivent etre uploadees.");
            return "redirect:/demandes/liste";
        }

        Integer scanTermineId = ensureStatus("scan termine", "scan terminé");
        addStatusHistory(demande, scanTermineId);
        redirectAttributes.addFlashAttribute("successMessage", "Scan termine pour la demande #" + id + ".");
        return "redirect:/demandes/liste";
    }

        @GetMapping("/details/{id}")
        public String afficherDetails(@PathVariable("id") Long id, Model model) {
        Demande demande = demandeRepository.findById(id.intValue())
            .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
            .orElseThrow(() -> new IllegalArgumentException("Demandeur introuvable"));
        Passeport passeport = demandeur.getIdPasseport();

            NumVisaTransformable numVisaTransformable = numVisaTransformableRepository
                .findFirstByIdDemandeur(demandeur.getIdDemandeur().intValue())
                .orElse(null);

            DemandeInvestisseur demandeInvestisseur = demandeInvestisseurRepository
                .findFirstByIdDemande(demande.getIdDemande().longValue())
                .orElse(null);
            ProjetInvestissement projetInvestissement = null;
            if (demandeInvestisseur != null && demandeInvestisseur.getIdProjet() != null) {
                projetInvestissement = projetInvestissementRepository
                    .findById(demandeInvestisseur.getIdProjet())
                    .orElse(null);
            }

            DemandeTravailleur demandeTravailleur = demandeTravailleurRepository
                .findFirstByIdDemande(demande.getIdDemande().longValue())
                .orElse(null);
            EmployeurMadagascar employeur = null;
            if (demandeTravailleur != null && demandeTravailleur.getIdEmployeur() != null) {
                employeur = employeurMadagascarRepository
                    .findById(demandeTravailleur.getIdEmployeur().intValue())
                    .orElse(null);
            }

            List<DemandePieceJustificative> piecesSelectionnees =
                demandePieceJustificativeRepository.findByIdDemande(demande.getIdDemande().longValue());
            List<PieceJustificative> piecesJustificatives;
            if (piecesSelectionnees.isEmpty()) {
                piecesJustificatives = List.of();
            } else {
                Set<Long> pieceIds = piecesSelectionnees.stream()
                    .map(DemandePieceJustificative::getIdPieceJustificative)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                piecesJustificatives = pieceJustificativeRepository.findAllById(pieceIds);
            }

        TypeVisa typeVisa = null;
        if (demande.getIdTypeVisa() != null) {
            typeVisa = typeVisaRepository.findById(demande.getIdTypeVisa().longValue()).orElse(null);
        }

        Map<Integer, StatusDemande> statuts = statusDemandeRepository.findAll()
            .stream()
            .collect(Collectors.toMap(StatusDemande::getIdStatus, Function.identity()));
        StatusDemande status = getLatestStatusForDemande(demande, statuts);
        boolean canEditPhotoSignature = isDossierCree(status);
        String photoIdentiteUrl = hasText(demandeur.getPhotoIdentite())
            ? "/demandes/" + id + "/demandeur/photo"
            : null;
        String signatureUrl = hasText(demandeur.getSignatureDigital())
            ? "/demandes/" + id + "/demandeur/signature"
            : null;

        model.addAttribute("demande", demande);
        model.addAttribute("demandeur", demandeur);
        model.addAttribute("passeport", passeport);
        model.addAttribute("typeVisa", typeVisa);
        model.addAttribute("numVisaTransformable", numVisaTransformable);
        model.addAttribute("demandeInvestisseur", demandeInvestisseur);
        model.addAttribute("projetInvestissement", projetInvestissement);
        model.addAttribute("demandeTravailleur", demandeTravailleur);
        model.addAttribute("employeur", employeur);
        model.addAttribute("piecesJustificatives", piecesJustificatives);
        model.addAttribute("photoIdentiteUrl", photoIdentiteUrl);
        model.addAttribute("signatureUrl", signatureUrl);
        model.addAttribute("canEditPhotoSignature", canEditPhotoSignature);
        return "details/demande-details";
        }

    @GetMapping("/{id}/photo-signature")
    public String afficherPhotoSignature(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Demandeur introuvable"));

        Map<Integer, StatusDemande> statuts = statusDemandeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(StatusDemande::getIdStatus, Function.identity()));
        StatusDemande status = getLatestStatusForDemande(demande, statuts);
        if (!isDossierCree(status)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Photo et signature modifiables uniquement au statut dossier cree.");
            return "redirect:/demandes/details/" + id;
        }

        String photoIdentiteUrl = hasText(demandeur.getPhotoIdentite())
                ? "/demandes/" + id + "/demandeur/photo"
                : null;
        String signatureUrl = hasText(demandeur.getSignatureDigital())
                ? "/demandes/" + id + "/demandeur/signature"
                : null;

        model.addAttribute("demande", demande);
        model.addAttribute("demandeur", demandeur);
        model.addAttribute("photoIdentiteUrl", photoIdentiteUrl);
        model.addAttribute("signatureUrl", signatureUrl);
        return "forms/demande-photo-signature";
    }

    @PostMapping("/{id}/photo-signature")
    public String enregistrerPhotoSignature(
            @PathVariable("id") Long id,
            @RequestParam(value = "photoData", required = false) String photoData,
            @RequestParam(value = "signatureData", required = false) String signatureData,
            RedirectAttributes redirectAttributes) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Demandeur introuvable"));

        Map<Integer, StatusDemande> statuts = statusDemandeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(StatusDemande::getIdStatus, Function.identity()));
        StatusDemande status = getLatestStatusForDemande(demande, statuts);
        if (!isDossierCree(status)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Photo et signature modifiables uniquement au statut dossier cree.");
            return "redirect:/demandes/details/" + id;
        }

        boolean updated = false;

        try {
            if (hasText(photoData)) {
                String storedName = storeDemandeurMedia(id, photoData, "photo");
                if (storedName != null) {
                    deleteDemandeurFileIfExists(id, demandeur.getPhotoIdentite());
                    demandeur.setPhotoIdentite(storedName);
                    updated = true;
                }
            }

            if (hasText(signatureData)) {
                String storedName = storeDemandeurMedia(id, signatureData, "signature");
                if (storedName != null) {
                    deleteDemandeurFileIfExists(id, demandeur.getSignatureDigital());
                    demandeur.setSignatureDigital(storedName);
                    updated = true;
                }
            }
        } catch (IllegalArgumentException | IOException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/demandes/" + id + "/photo-signature";
        }

        if (!updated) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucune capture a enregistrer.");
            return "redirect:/demandes/" + id + "/photo-signature";
        }

        demandeurRepository.save(demandeur);
        redirectAttributes.addFlashAttribute("successMessage", "Photo et signature enregistrees avec succes.");
        return "redirect:/demandes/details/" + id;
    }

    @GetMapping("/{id}/demandeur/{type}")
    @ResponseBody
    public ResponseEntity<Resource> lireMediaDemandeur(
            @PathVariable("id") Long id,
            @PathVariable("type") String type) throws IOException {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Demandeur introuvable"));

        String fileName = null;
        if ("photo".equalsIgnoreCase(type)) {
            fileName = demandeur.getPhotoIdentite();
        } else if ("signature".equalsIgnoreCase(type)) {
            fileName = demandeur.getSignatureDigital();
        }

        if (!hasText(fileName)) {
            return ResponseEntity.notFound().build();
        }

        Path mediaDir = getDemandeurMediaDir(id);
        Path filePath = mediaDir.resolve(Paths.get(fileName).getFileName().toString()).normalize();
        if (!filePath.startsWith(mediaDir) || !Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        MediaType mediaType = contentType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/modifier/{id}")
    public String afficherFormulaireModification(@PathVariable("id") Long id, Model model) {
        Demande demande = demandeRepository.findById(id.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
        if (isScanTermine(demande)) {
            throw new IllegalArgumentException("Modification impossible: scan deja termine pour cette demande.");
        }
        
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
        if (isScanTermine(demande)) {
            throw new IllegalArgumentException("Modification impossible: scan deja termine pour cette demande.");
        }
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
                boolean existeDeja = demandePieceJustificativeRepository
                        .findFirstByIdDemandeAndIdPieceJustificative(id, pieceId)
                        .isPresent();

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

    private Set<Integer> extractIdsInt(List<Demande> demandes, Function<Demande, Integer> extractor) {
        return demandes.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Long> extractIdsLong(List<Demande> demandes, Function<Demande, Integer> extractor) {
        return demandes.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .map(Integer::longValue)
                .collect(Collectors.toSet());
    }

    private Map<Integer, Boolean> buildPiecesCompletesByDemande(List<Demande> demandes) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (Demande demande : demandes) {
            List<DemandePieceJustificative> pieces =
                    demandePieceJustificativeRepository.findByIdDemande(demande.getIdDemande().longValue());
            result.put(demande.getIdDemande(), areAllPiecesUploaded(pieces));
        }
        return result;
    }

    private Map<Integer, Boolean> buildPhotoSignatureCompletesByDemande(
            List<Demande> demandes,
            Map<Integer, Demandeur> demandeurs) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (Demande demande : demandes) {
            Demandeur demandeur = demandeurs.get(demande.getIdDemandeur());
            result.put(demande.getIdDemande(), hasPhotoAndSignature(demandeur));
        }
        return result;
    }

    private Map<Integer, Boolean> buildScanTermineByDemande(List<Demande> demandes, Map<Integer, StatusDemande> statuts) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (Demande demande : demandes) {
            StatusDemande status = getLatestStatusForDemande(demande, statuts);
            result.put(demande.getIdDemande(), isScanTermine(status));
        }
        return result;
    }

    private Map<Integer, Boolean> buildVisaApprouveByDemande(List<Demande> demandes, Map<Integer, StatusDemande> statuts) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (Demande demande : demandes) {
            StatusDemande status = getLatestStatusForDemande(demande, statuts);
            result.put(demande.getIdDemande(), isVisaApprouve(status));
        }
        return result;
    }

    private Map<Integer, String> buildLatestStatusLabelByDemande(List<Demande> demandes, Map<Integer, StatusDemande> statuts) {
        Map<Integer, String> result = new HashMap<>();
        for (Demande demande : demandes) {
            StatusDemande status = getLatestStatusForDemande(demande, statuts);
            result.put(demande.getIdDemande(), status == null ? "N/A" : status.getStatus());
        }
        return result;
    }

    private boolean areAllPiecesUploaded(List<DemandePieceJustificative> pieces) {
        if (pieces.isEmpty()) {
            return false;
        }
        Map<Long, Boolean> byPiece = new HashMap<>();
        for (DemandePieceJustificative piece : pieces) {
            Long pieceId = piece.getIdPieceJustificative();
            if (pieceId == null) {
                continue;
            }
            boolean uploaded = !parseUploadedFiles(piece.getPhotoPieceJustificative()).isEmpty();
            if (uploaded) {
                byPiece.put(pieceId, true);
            } else if (!byPiece.containsKey(pieceId)) {
                byPiece.put(pieceId, false);
            }
        }
        if (byPiece.isEmpty()) {
            return false;
        }
        return byPiece.values().stream().allMatch(Boolean::booleanValue);
    }

    private List<DemandePieceJustificative> dedupePieces(List<DemandePieceJustificative> pieces) {
        Map<Long, DemandePieceJustificative> unique = new LinkedHashMap<>();
        for (DemandePieceJustificative piece : pieces) {
            Long pieceId = piece.getIdPieceJustificative();
            if (pieceId == null) {
                continue;
            }
            DemandePieceJustificative existing = unique.get(pieceId);
            if (existing == null || (!hasUploadedFiles(existing) && hasUploadedFiles(piece))) {
                unique.put(pieceId, piece);
            }
        }
        return new ArrayList<>(unique.values());
    }

    private boolean hasUploadedFiles(DemandePieceJustificative piece) {
        if (piece == null) {
            return false;
        }
        return !parseUploadedFiles(piece.getPhotoPieceJustificative()).isEmpty();
    }

    private List<String> parseUploadedFiles(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(value.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private boolean isAllEmpty(MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private List<String> replacePieceFiles(Long demandeId, Long pieceId, MultipartFile[] files) throws IOException {
        Path pieceDir = uploadRoot.resolve(String.valueOf(demandeId)).resolve(String.valueOf(pieceId));
        Files.createDirectories(pieceDir);

        try (var existing = Files.list(pieceDir)) {
            existing.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        }

        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String original = file.getOriginalFilename() == null ? "document" : Paths.get(file.getOriginalFilename()).getFileName().toString();
            String storedName = System.currentTimeMillis() + "-" + original;
            Files.copy(file.getInputStream(), pieceDir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
            fileNames.add(storedName);
        }

        return fileNames;
    }

    private Integer ensureStatus(String... labels) {
        Integer statusId = findStatusId(labels);
        if (statusId != null) {
            return statusId;
        }

        StatusDemande created = new StatusDemande();
        created.setStatus(labels.length > 0 ? labels[0] : "scan termine");
        return statusDemandeRepository.save(created).getIdStatus();
    }

    private Integer findStatusId(String... labels) {
        List<StatusDemande> all = statusDemandeRepository.findAll();
        for (String label : labels) {
            String normalizedLabel = normalize(label);
            for (StatusDemande status : all) {
                if (normalize(status.getStatus()).equals(normalizedLabel)) {
                    return status.getIdStatus();
                }
            }
        }
        return null;
    }

    private boolean isScanTermine(Demande demande) {
        if (demande == null) {
            return false;
        }
        StatusDemande status = getLatestStatusForDemande(demande, statusDemandeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(StatusDemande::getIdStatus, Function.identity())));
        return isScanTermine(status);
    }

    private StatusDemande getLatestStatusForDemande(Demande demande, Map<Integer, StatusDemande> statuts) {
        if (demande == null || demande.getIdDemande() == null) {
            return null;
        }
        return demandeStatusHistoryRepository
                .findFirstByIdDemandeOrderByDateChangementStatusDesc(demande.getIdDemande().longValue())
                .map(history -> statuts.get(history.getIdStatus()))
                .orElse(null);
    }

    private void addStatusHistory(Demande demande, Integer statusId) {
        if (demande == null || demande.getIdDemande() == null || statusId == null) {
            return;
        }
        DemandeStatusHistory history = new DemandeStatusHistory();
        history.setIdDemande(demande.getIdDemande().longValue());
        history.setIdStatus(statusId);
        history.setDateChangementStatus(new Date());
        demandeStatusHistoryRepository.save(history);
    }

    private boolean isDossierCree(StatusDemande status) {
        if (status == null) {
            return false;
        }
        String normalized = normalize(status.getStatus());
        return normalized.equals("dossier cree");
    }

    private boolean hasPhotoAndSignature(Demandeur demandeur) {
        if (demandeur == null) {
            return false;
        }
        return hasText(demandeur.getPhotoIdentite()) && hasText(demandeur.getSignatureDigital());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Path getDemandeurMediaDir(Long demandeId) {
        return uploadRoot.resolve(String.valueOf(demandeId)).resolve("demandeur");
    }

    private String storeDemandeurMedia(Long demandeId, String dataUrl, String prefix) throws IOException {
        DataUrlPayload payload = parseDataUrl(dataUrl);
        if (payload == null || payload.data == null || payload.data.length == 0) {
            return null;
        }
        if (payload.mimeType == null || !payload.mimeType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Format d'image invalide.");
        }

        Path mediaDir = getDemandeurMediaDir(demandeId);
        Files.createDirectories(mediaDir);

        String extension = resolveImageExtension(payload.mimeType);
        String storedName = prefix + "-" + System.currentTimeMillis() + "." + extension;
        Path filePath = mediaDir.resolve(storedName).normalize();

        if (!filePath.startsWith(mediaDir)) {
            throw new IllegalArgumentException("Nom de fichier invalide.");
        }

        Files.write(filePath, payload.data);
        return storedName;
    }

    private DataUrlPayload parseDataUrl(String dataUrl) {
        if (!hasText(dataUrl)) {
            return null;
        }

        String trimmed = dataUrl.trim();
        if (!trimmed.startsWith("data:")) {
            throw new IllegalArgumentException("Donnees image invalides.");
        }

        int base64Index = trimmed.indexOf(";base64,");
        if (base64Index < 0) {
            throw new IllegalArgumentException("Donnees image invalides.");
        }

        String mimeType = trimmed.substring(5, base64Index);
        String base64 = trimmed.substring(base64Index + 8);
        if (!hasText(base64)) {
            return null;
        }

        byte[] data;
        try {
            data = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Donnees image invalides.");
        }

        DataUrlPayload payload = new DataUrlPayload();
        payload.mimeType = mimeType;
        payload.data = data;
        return payload;
    }

    private String resolveImageExtension(String mimeType) {
        if (mimeType == null) {
            return "png";
        }

        String normalized = mimeType.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/webp" -> "webp";
            default -> "png";
        };
    }

    private void deleteDemandeurFileIfExists(Long demandeId, String fileName) throws IOException {
        if (!hasText(fileName)) {
            return;
        }

        Path mediaDir = getDemandeurMediaDir(demandeId);
        Path filePath = mediaDir.resolve(Paths.get(fileName).getFileName().toString()).normalize();
        if (filePath.startsWith(mediaDir)) {
            Files.deleteIfExists(filePath);
        }
    }

    private boolean isScanTermine(StatusDemande status) {
        if (status == null) {
            return false;
        }
        String normalized = normalize(status.getStatus());
        return normalized.equals("scan termine");
    }

    private boolean isVisaApprouve(StatusDemande status) {
        if (status == null) {
            return false;
        }
        String normalized = normalize(status.getStatus());
        return normalized.equals("visa approuve") || normalized.equals("visa approuvee");
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return normalized.trim().toLowerCase();
    }

    private boolean isImageFile(String fileName) {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        return lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png")
                || lower.endsWith(".gif")
                || lower.endsWith(".webp")
                || lower.endsWith(".bmp");
    }

    private static class UploadedFileView {
        private final String name;
        private final String url;
        private final boolean image;

        private UploadedFileView(String name, String url, boolean image) {
            this.name = name;
            this.url = url;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public boolean isImage() {
            return image;
        }
    }

    private static class DataUrlPayload {
        private String mimeType;
        private byte[] data;
    }
}