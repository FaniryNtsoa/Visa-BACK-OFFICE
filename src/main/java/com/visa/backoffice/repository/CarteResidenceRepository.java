package com.visa.backoffice.repository;

import com.visa.backoffice.model.CarteResidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteResidenceRepository extends JpaRepository<CarteResidence, Long> {
}