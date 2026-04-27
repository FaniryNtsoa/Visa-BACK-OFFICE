package com.visa.backoffice.repository;

import com.visa.backoffice.model.DemandeInvestisseur;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeInvestisseurRepository extends JpaRepository<DemandeInvestisseur, Long> {
	Optional<DemandeInvestisseur> findFirstByIdDemande(Long idDemande);
}