package com.visa.backoffice.repository;

import com.visa.backoffice.model.Obligatoire;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObligatoireRepository extends JpaRepository<Obligatoire, Long> {
	List<Obligatoire> findByIdTypeVisa_IdTypeVisa(Long idTypeVisa);
}