package com.visa.backoffice.repository;

import com.visa.backoffice.model.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Long> {
}