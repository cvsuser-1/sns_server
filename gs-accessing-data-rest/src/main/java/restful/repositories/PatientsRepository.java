package restful.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import restful.common.Patient;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PatientsRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);
}
