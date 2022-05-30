package com.JDR.Vacunassist.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JDR.Vacunassist.Model.Vacuna;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Long>{

}
