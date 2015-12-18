package restful.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import restful.common.Caregiver;

public interface CaregiversRepository extends JpaRepository<Caregiver, Long> {
    Optional<Caregiver> findByUsername(String username);
}