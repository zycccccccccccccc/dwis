package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.ScrapRecord;

public interface ScrapRecordRepository extends JpaRepository<ScrapRecord, Integer>, JpaSpecificationExecutor<ScrapRecord> {

}
