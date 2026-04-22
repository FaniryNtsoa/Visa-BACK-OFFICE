package com.visa.backoffice.repository;

import com.visa.backoffice.model.EmployeurMadagascar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeurMadagascarRepository extends JpaRepository<EmployeurMadagascar, Integer> {
}