package com.visa.backoffice.repository;

import com.visa.backoffice.model.DemandeStatusHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeStatusHistoryRepository extends JpaRepository<DemandeStatusHistory, Integer> {
    Optional<DemandeStatusHistory> findFirstByIdDemandeOrderByDateChangementStatusDesc(Long idDemande);
}
