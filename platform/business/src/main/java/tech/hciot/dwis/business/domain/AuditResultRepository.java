package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.AuditResult;

public interface AuditResultRepository extends JpaRepository<AuditResult, Integer>, JpaSpecificationExecutor<AuditResult> {

  List<AuditResult> findByAuditBatch(String auditBatch);
}
