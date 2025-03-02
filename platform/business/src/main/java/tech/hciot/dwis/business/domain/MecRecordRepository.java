package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.MecRecord;

public interface MecRecordRepository extends JpaRepository<MecRecord, Integer>, JpaSpecificationExecutor<MecRecord> {

}
