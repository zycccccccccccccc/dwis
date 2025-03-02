package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.AccountDetail;

public interface AccountDetailRepository extends JpaRepository<AccountDetail, String>, JpaSpecificationExecutor<AccountDetail> {

}
