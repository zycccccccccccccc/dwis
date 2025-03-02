package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.AccountRoleView;

public interface AccountRoleViewRepository extends JpaRepository<AccountRoleView, String>, JpaSpecificationExecutor<AccountRoleView> {

}
