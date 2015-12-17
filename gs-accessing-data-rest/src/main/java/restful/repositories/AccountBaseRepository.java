package restful.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import restful.common.AccountBase;


public interface AccountBaseRepository extends JpaRepository<AccountBase, Long> {
    Optional<AccountBase> findByUsername(String username);
}
