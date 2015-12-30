package restful.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import restful.model.common.Clinician;

public interface CliniciansRepository extends JpaRepository<Clinician, Long> {
  Optional<Clinician> findByUsername(String username);
}
