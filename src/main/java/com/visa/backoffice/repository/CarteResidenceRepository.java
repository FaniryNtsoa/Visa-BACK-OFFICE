package com.visa.backoffice.repository;

import com.visa.backoffice.model.CarteResidence;
import com.visa.backoffice.model.Passeport;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteResidenceRepository extends JpaRepository<CarteResidence, Long> {
	Optional<CarteResidence> findFirstByPasseportOrderByIdCarteResidenceDesc(Passeport passeport);
	Optional<CarteResidence> findFirstByNumeroCarteOrderByIdCarteResidenceDesc(String numeroCarte);
}