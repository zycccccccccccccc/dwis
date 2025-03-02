package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.XNReleaseRecord;

public interface XNReleaseRecordRepository extends JpaRepository<XNReleaseRecord, Integer>, JpaSpecificationExecutor<XNReleaseRecord> {

}
