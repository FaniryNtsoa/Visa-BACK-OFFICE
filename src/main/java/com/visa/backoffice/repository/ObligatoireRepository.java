package com.visa.backoffice.repository;

import com.visa.backoffice.model.Obligatoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObligatoireRepository extends JpaRepository<Obligatoire, Long> {
}