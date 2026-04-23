package com.visa.backoffice.repository;

import com.visa.backoffice.model.Visa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisaRepository extends JpaRepository<Visa, Long> {
	Optional<Visa> findFirstByNumeroVisa(String numeroVisa);
	Optional<Visa> findFirstByDemande_IdDemande(Integer idDemande);
}