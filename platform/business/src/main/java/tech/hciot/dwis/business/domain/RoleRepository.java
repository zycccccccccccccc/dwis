package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.hciot.dwis.business.domain.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

  int countByRoleName(String roleName);
}
