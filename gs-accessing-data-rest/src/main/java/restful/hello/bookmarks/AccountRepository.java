package restful.hello.bookmarks;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    //Optional<Account> findByUsername(String username);
	List<Account> findByUsername(String username);
}
