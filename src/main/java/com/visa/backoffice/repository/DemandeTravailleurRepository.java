package com.visa.backoffice.repository;

import com.visa.backoffice.model.DemandeTravailleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeTravailleurRepository extends JpaRepository<DemandeTravailleur, Long> {
}