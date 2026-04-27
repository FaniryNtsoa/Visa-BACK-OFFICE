package com.visa.backoffice.repository;

import com.visa.backoffice.model.TypeDemande;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Integer> {
	Optional<TypeDemande> findFirstByTypeDemandeIgnoreCase(String typeDemande);
}