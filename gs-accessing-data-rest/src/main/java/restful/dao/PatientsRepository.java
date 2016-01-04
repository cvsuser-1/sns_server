package restful.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import restful.model.common.Patient;

public interface PatientsRepository extends JpaRepository<Patient, Long> {
  Optional<Patient> findByUsername(String username);
}
