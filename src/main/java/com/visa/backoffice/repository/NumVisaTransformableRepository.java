package com.visa.backoffice.repository;

import com.visa.backoffice.model.NumVisaTransformable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NumVisaTransformableRepository extends JpaRepository<NumVisaTransformable, Long> {
	Optional<NumVisaTransformable> findFirstByIdDemandeur(Integer idDemandeur);
}