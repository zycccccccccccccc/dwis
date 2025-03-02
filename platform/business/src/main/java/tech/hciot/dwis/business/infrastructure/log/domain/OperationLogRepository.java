package tech.hciot.dwis.business.infrastructure.log.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.infrastructure.log.domain.model.OperationLog;

public interface OperationLogRepository extends JpaRepository<OperationLog, String>, JpaSpecificationExecutor<OperationLog> {

}
