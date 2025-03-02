package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.hciot.dwis.business.domain.model.AccountRole;

public interface AccountRoleRepository extends JpaRepository<AccountRole, String> {

  int countByRoleId(String roleId);

  void deleteByAccountId(String accountId);
}
