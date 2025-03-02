package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.ScrapReasonRecord;

public interface ScrapReasonRecordRepository extends JpaRepository<ScrapReasonRecord, Integer>,
    JpaSpecificationExecutor<ScrapReasonRecord> {

}
