package com.visa.backoffice.web;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visa.backoffice.model.Demande;
import com.visa.backoffice.model.DemandeStatusHistory;
import com.visa.backoffice.service.DemandeApiService;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080" })
public class DemandeApiController {

    private final DemandeApiService demandeApiService;

    public DemandeApiController(DemandeApiService demandeApiService) {
        this.demandeApiService = demandeApiService;
    }

    @GetMapping("/{numero}")
    public ResponseEntity<List<Demande>> getDemandeList(@PathVariable String numero) {
        List<Demande> demandes = demandeApiService.getAllByNumero(numero);

        if (demandes == null || demandes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Map<String, Object>> getDemandeDetails(@PathVariable("id") int id) {
        Map<String, Object> details = demandeApiService.getDemandeDetails(id);

        if (details != null) {
            return ResponseEntity.ok(details);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<List<DemandeStatusHistory>> showDemandeStatus(@PathVariable("id") int id) {
        List<DemandeStatusHistory> statusHistory = demandeApiService.getDemandeStatus(id);

        if (statusHistory != null) {
            return ResponseEntity.ok(statusHistory);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
