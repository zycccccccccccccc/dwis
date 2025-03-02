package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.HLDReinspRecord;

public interface HLDReinspRecordRepository extends JpaRepository<HLDReinspRecord, Integer>, JpaSpecificationExecutor<HLDReinspRecord> {
}
