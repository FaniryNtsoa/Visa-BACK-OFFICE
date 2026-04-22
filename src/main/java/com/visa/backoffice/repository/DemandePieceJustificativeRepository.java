package com.visa.backoffice.repository;

import com.visa.backoffice.model.DemandePieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandePieceJustificativeRepository extends JpaRepository<DemandePieceJustificative, Long> {
}