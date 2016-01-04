package restful.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import restful.model.common.Caregiver;

public interface CaregiversRepository extends JpaRepository<Caregiver, Long> {
  Optional<Caregiver> findByUsername(String username);
}