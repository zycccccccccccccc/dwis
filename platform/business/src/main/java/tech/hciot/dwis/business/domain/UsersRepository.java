package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.User;

public interface UsersRepository extends JpaRepository<User, String>,
    JpaSpecificationExecutor<User> {

  User findByUsername(String username);

  Optional<User> findByIdOrUsername(String id, String username);
}
