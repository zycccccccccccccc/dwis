package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.AuditDetail;

public interface AuditDetailRepository extends JpaRepository<AuditDetail, Integer>, JpaSpecificationExecutor<AuditDetail> {

}
