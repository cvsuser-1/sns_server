package restful.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import restful.common.Clinician;

public interface CliniciansRepository extends JpaRepository<Clinician, Long> {
    Optional<Clinician> findByUsername(String username);
}
