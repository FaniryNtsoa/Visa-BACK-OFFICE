package com.visa.backoffice.repository;

import com.visa.backoffice.model.DemandePieceJustificative;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandePieceJustificativeRepository extends JpaRepository<DemandePieceJustificative, Long> {
	List<DemandePieceJustificative> findByIdDemande(Long idDemande);
	Optional<DemandePieceJustificative> findFirstByIdDemandeAndIdPieceJustificative(Long idDemande, Long idPieceJustificative);
}