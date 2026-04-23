package com.visa.backoffice.repository;

import com.visa.backoffice.model.StatusDemande;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDemandeRepository extends JpaRepository<StatusDemande, Integer> {
	Optional<StatusDemande> findFirstByStatusIgnoreCase(String status);
}