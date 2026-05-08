package com.visa.backoffice.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visa.backoffice.model.Obligatoire;
import com.visa.backoffice.model.TypeVisa;
import com.visa.backoffice.repository.NationaliteRepository;
import com.visa.backoffice.repository.ObligatoireRepository;
import com.visa.backoffice.repository.PieceJustificativeRepository;
import com.visa.backoffice.repository.SituationFamilialeRepository;
import com.visa.backoffice.repository.TypeVisaRepository;
import com.visa.backoffice.service.DemandeCreationService;
import com.visa.backoffice.service.FormResult;
import com.visa.backoffice.web.form.DuplicataForm;
import com.visa.backoffice.web.form.NouvelleDemandeForm;
import com.visa.backoffice.web.form.TransfertVisaForm;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demandes")
public class FormulaireController {

    private final DemandeCreationService demandeCreationService;
    private final TypeVisaRepository typeVisaRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final ObligatoireRepository obligatoireRepository;
    private final ObjectMapper objectMapper;
    private final String frontendBaseUrl;

    public FormulaireController(
            DemandeCreationService demandeCreationService,
            TypeVisaRepository typeVisaRepository,
            PieceJustificativeRepository pieceJustificativeRepository,
            SituationFamilialeRepository situationFamilialeRepository,
            NationaliteRepository nationaliteRepository,
            ObligatoireRepository obligatoireRepository,
            ObjectMapper objectMapper,
            @Value("${app.frontend-base-url:http://localhost:4200}") String frontendBaseUrl) {
        this.demandeCreationService = demandeCreationService;
        this.typeVisaRepository = typeVisaRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.obligatoireRepository = obligatoireRepository;
        this.objectMapper = objectMapper;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @GetMapping("/nouvelle")
    public String nouvelleDemande(Model model) {
        model.addAttribute("form", new NouvelleDemandeForm());
        populateNouvelleDemandeModel(model);
        return "forms/nouvelle-demande";
    }

    @PostMapping("/nouvelle")
    public String enregistrerNouvelleDemande(@ModelAttribute("form") NouvelleDemandeForm form, Model model) {
        try {
            FormResult result = demandeCreationService.creerNouvelleDemande(form);
            List<Long> createdIds = result.getDemandeIds();
            if (createdIds != null && !createdIds.isEmpty()) {
                return "redirect:/demandes/qr/" + createdIds.get(0);
            }
            return "redirect:/demandes/liste";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateNouvelleDemandeModel(model);
            return "forms/nouvelle-demande";
        }
    }

    @GetMapping("/qr/{id}")
    public String afficherQrDemande(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String frontendQrUrl = buildFrontendQrUrl(id);
        String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=260x260&data="
                + URLEncoder.encode(frontendQrUrl, StandardCharsets.UTF_8);

        model.addAttribute("demandeId", id);
        model.addAttribute("frontendQrUrl", frontendQrUrl);
        model.addAttribute("qrImageUrl", qrImageUrl);
        model.addAttribute("retourListeUrl", request.getContextPath() + "/demandes/liste");
        return "forms/demande-qr";
    }

    @GetMapping("/transfert")
    public String transfertVisa(Model model) {
        model.addAttribute("form", new TransfertVisaForm());
        return "forms/transfert-visa";
    }

    @PostMapping("/transfert")
    public String enregistrerTransfertVisa(@ModelAttribute("form") TransfertVisaForm form, Model model) {
        try {
            demandeCreationService.creerTransfertVisa(form);
            return "redirect:/demandes/liste";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "forms/transfert-visa";
        }
    }

    @GetMapping("/duplicata")
    public String duplicata(Model model) {
        model.addAttribute("form", new DuplicataForm());
        return "forms/duplicata";
    }

    @PostMapping("/duplicata")
    public String enregistrerDuplicata(@ModelAttribute("form") DuplicataForm form, Model model) {
        try {
            demandeCreationService.creerDuplicata(form);
            return "redirect:/demandes/liste";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "forms/duplicata";
        }
    }

    @GetMapping("/transfert-sans-donnees")
    public String transfertSansDonnees(Model model) {
        model.addAttribute("form", new NouvelleDemandeForm());
        populateNouvelleDemandeModel(model);
        return "forms/transfert-visa-sans-donnees";
    }

    @PostMapping("/transfert-sans-donnees")
    public String enregistrerTransfertSansDonnees(@ModelAttribute("form") NouvelleDemandeForm form, Model model) {
        try {
            demandeCreationService.creerTransfertVisaSansDonnees(form);
            return "redirect:/demandes/liste";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateNouvelleDemandeModel(model);
            return "forms/transfert-visa-sans-donnees";
        }
    }

    @GetMapping("/duplicata-sans-donnees")
    public String duplicataSansDonnees(Model model) {
        model.addAttribute("form", new NouvelleDemandeForm());
        populateNouvelleDemandeModel(model);
        return "forms/duplicata-sans-donnees";
    }

    @PostMapping("/duplicata-sans-donnees")
    public String enregistrerDuplicataSansDonnees(@ModelAttribute("form") NouvelleDemandeForm form, Model model) {
        try {
            demandeCreationService.creerDuplicataSansDonnees(form);
            return "redirect:/demandes/liste";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateNouvelleDemandeModel(model);
            return "forms/duplicata-sans-donnees";
        }
    }

    private void populateNouvelleDemandeModel(Model model) {
        List<TypeVisa> typeVisas = typeVisaRepository.findAll();
        model.addAttribute("typeVisas", typeVisas);
        model.addAttribute("pieces", pieceJustificativeRepository.findAll());
        model.addAttribute("situations", situationFamilialeRepository.findAll());
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("typeVisaCategories", buildTypeVisaCategories(typeVisas));
        model.addAttribute("obligatoiresJson", buildObligatoiresJson(typeVisas));
    }

    private Map<Long, String> buildTypeVisaCategories(List<TypeVisa> typeVisas) {
        Map<Long, String> categories = new HashMap<>();
        for (TypeVisa typeVisa : typeVisas) {
            String label = typeVisa.getTypeVisa() == null ? "" : typeVisa.getTypeVisa().toLowerCase(Locale.ROOT);
            if (label.contains("invest")) {
                categories.put(typeVisa.getIdTypeVisa(), "investisseur");
            } else if (label.contains("travail")) {
                categories.put(typeVisa.getIdTypeVisa(), "travailleur");
            } else {
                categories.put(typeVisa.getIdTypeVisa(), "autre");
            }
        }
        return categories;
    }

    private String buildObligatoiresJson(List<TypeVisa> typeVisas) {
        Map<Long, List<String>> obligatoiresByType = new HashMap<>();
        for (TypeVisa typeVisa : typeVisas) {
            List<Obligatoire> obligatoires = obligatoireRepository.findByIdTypeVisa_IdTypeVisa(typeVisa.getIdTypeVisa());
            List<String> fields = new ArrayList<>();
            for (Obligatoire obligatoire : obligatoires) {
                String key = obligatoire.getNomTable() + "." + obligatoire.getNomColonneObligatoire();
                fields.add(key);
            }
            obligatoiresByType.put(typeVisa.getIdTypeVisa(), fields);
        }

        try {
            return objectMapper.writeValueAsString(obligatoiresByType);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private String confirmation(Model model, FormResult result) {
        model.addAttribute("message", result.getMessage());
        model.addAttribute("demandeIds", result.getDemandeIds());
        return "forms/confirmation";
    }

    private String buildFrontendQrUrl(Long id) {
        String base = frontendBaseUrl == null ? "http://localhost:4200" : frontendBaseUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/qr/" + id;
    }
}
