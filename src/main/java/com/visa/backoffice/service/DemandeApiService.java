package com.visa.backoffice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.visa.backoffice.model.*;
import com.visa.backoffice.repository.CarteResidenceRepository;
import com.visa.backoffice.repository.DemandeInvestisseurRepository;
import com.visa.backoffice.repository.DemandePieceJustificativeRepository;
import com.visa.backoffice.repository.DemandeRepository;
import com.visa.backoffice.repository.DemandeStatusHistoryRepository;
import com.visa.backoffice.repository.DemandeTravailleurRepository;
import com.visa.backoffice.repository.DemandeurRepository;
import com.visa.backoffice.repository.EmployeurMadagascarRepository;
import com.visa.backoffice.repository.NationaliteRepository;
import com.visa.backoffice.repository.NumVisaTransformableRepository;
import com.visa.backoffice.repository.PasseportRepository;
import com.visa.backoffice.repository.PieceJustificativeRepository;
import com.visa.backoffice.repository.ProjetInvestissementRepository;
import com.visa.backoffice.repository.SituationFamilialeRepository;
import com.visa.backoffice.repository.StatusDemandeRepository;
import com.visa.backoffice.repository.TypeDemandeRepository;
import com.visa.backoffice.repository.TypeVisaRepository;
import com.visa.backoffice.repository.VisaRepository;

@Service
public class DemandeApiService {
    
    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatusDemandeRepository statusDemandeRepository;
    private final PasseportRepository passeportRepository;
    private final DemandeStatusHistoryRepository demandeStatusHistoryRepository;

    public DemandeApiService(DemandeRepository demandeRepository, DemandeurRepository demandeurRepository,
            TypeVisaRepository typeVisaRepository, TypeDemandeRepository typeDemandeRepository,
            StatusDemandeRepository statusDemandeRepository, SituationFamilialeRepository situationFamilialeRepository,
            NationaliteRepository nationaliteRepository, PasseportRepository passeportRepository,
            NumVisaTransformableRepository numVisaTransformableRepository,
            PieceJustificativeRepository pieceJustificativeRepository,
            DemandePieceJustificativeRepository demandePieceJustificativeRepository,
            ProjetInvestissementRepository projetInvestissementRepository,
            DemandeInvestisseurRepository demandeInvestisseurRepository,
            EmployeurMadagascarRepository employeurMadagascarRepository,
            DemandeTravailleurRepository demandeTravailleurRepository,
            DemandeStatusHistoryRepository demandeStatusHistoryRepository) {
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.statusDemandeRepository = statusDemandeRepository;
        this.passeportRepository = passeportRepository;
        this.demandeStatusHistoryRepository = demandeStatusHistoryRepository;
    }

    public int checkNumPasseportOrNumDemande(String numero) {
        try {
            if (passeportRepository.findByNumeroPasseport(numero).isPresent()) {
                return 1; // Numéro de passeport existe
            } else if (demandeRepository.findByNumeroDemande(numero).isPresent()) {
                return 2; // Numéro de demande existe
            } else {
                return 0; // Aucun des deux n'existe
            }
        } catch (Exception e) {
            return 0;   
        }
    }

    public List<Demande> getAllByNumero (String numero) {

        int result = checkNumPasseportOrNumDemande(numero);
        List<Demande> demandesList = new ArrayList<>();

        if (result == 1) {
            return getAllByPasseport(numero);
        }   else if (result == 2) {
            return getAllByDemande(numero);
        } else {
            return null;
        }

    }

    public List<Demande> getAllByPasseport (String numero) {
        Passeport passeport = passeportRepository.findByNumeroPasseport(numero).get();
        List<Demande> demandes = demandeRepository.findByNumeroPasseport(numero);
        return demandes;
    }

    public List<Demande> getAllByDemande (String numero) {
        Demande demande = demandeRepository.findByNumeroDemande(numero).get();
        List<Demande> demandesList = new ArrayList<>();
        demandesList.add(demande);
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue()).get();
        List<Demande> demandes = demandeRepository.findByNumeroPasseport(demandeur.getIdPasseport().getNumeroPasseport());
        for (Demande d : demandes) {
            if (!d.getNumeroDemande().equalsIgnoreCase(numero)) {
                demandesList.add(d);
            }
        }
        return demandesList;
    }

    public Map<String, Object> getDemandeDetails (int id) {
        Map<String, Object> details = new HashMap<>();
        
        Demande demande = demandeRepository.findById(id).orElse(null);
        if (demande == null) {
            return null;
        }
        Demandeur demandeur = demandeurRepository.findById(demande.getIdDemandeur().longValue()).orElse(null);
        TypeVisa typeVisa = typeVisaRepository.findById(demande.getIdTypeVisa().longValue()).orElse(null);
        TypeDemande typeDemande = typeDemandeRepository.findById(demande.getIdTypeDemande()).orElse(null);
        List<DemandeStatusHistory> demandeStatusHistory = demandeStatusHistoryRepository.findByIdDemande(Integer.valueOf(id).longValue());
        populateStatusLabels(demandeStatusHistory);


        details.put("demande", demande);
        details.put("demandeur", demandeur);
        details.put("typeVisa", typeVisa);
        details.put("typeDemande", typeDemande);
        details.put("demandeStatusHistory", demandeStatusHistory);

        return details;
    }

    public List<DemandeStatusHistory> getDemandeStatus (int id) {
        List<DemandeStatusHistory> history = demandeStatusHistoryRepository.findByIdDemande(Integer.valueOf(id).longValue());
        populateStatusLabels(history);
        return history;
    }

    private void populateStatusLabels(List<DemandeStatusHistory> history) {
        if (history == null || history.isEmpty()) {
            return;
        }

        Set<Integer> statusIds = history.stream()
                .map(DemandeStatusHistory::getIdStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (statusIds.isEmpty()) {
            return;
        }

        Map<Integer, StatusDemande> statusById = statusDemandeRepository.findAllById(statusIds)
                .stream()
                .collect(Collectors.toMap(StatusDemande::getIdStatus, s -> s));

        for (DemandeStatusHistory entry : history) {
            Integer statusId = entry.getIdStatus();
            if (statusId == null) {
                continue;
            }
            StatusDemande status = statusById.get(statusId);
            if (status != null) {
                entry.setStatusLabel(status.getStatus());
            }
        }
    }

}
