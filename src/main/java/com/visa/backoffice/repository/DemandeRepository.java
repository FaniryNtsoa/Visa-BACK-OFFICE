package com.visa.backoffice.repository;

import com.visa.backoffice.model.Demande;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {
	@Query(
		value = "select d.* from demande d "
			+ "join demandeur de on de.id = d.id_demandeur "
			+ "join passeport p on p.id = de.id_passeport "
			+ "where p.numero_passeport = :numeroPasseport "
			+ "order by d.id desc",
		nativeQuery = true)
	List<Demande> findByNumeroPasseport(@Param("numeroPasseport") String numeroPasseport);

	Optional<Demande> findByNumeroDemande(String numeroDemande);
}