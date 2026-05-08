package com.visa.backoffice.repository;

import com.visa.backoffice.model.Demande;
import com.visa.backoffice.model.Passeport;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Long> {

    Optional<Passeport> findByNumeroPasseport(String numeroPasseport);
}