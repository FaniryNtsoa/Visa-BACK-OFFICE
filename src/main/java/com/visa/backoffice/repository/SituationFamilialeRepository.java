package com.visa.backoffice.repository;

import com.visa.backoffice.model.SituationFamiliale;
import com.visa.backoffice.model.Visa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SituationFamilialeRepository extends JpaRepository<SituationFamiliale, Long> {

    Optional<SituationFamiliale> findById(Long situationFamilialeId);
}