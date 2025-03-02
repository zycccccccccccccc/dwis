package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.MoldPreShiftRecord;

public interface MoldPreShiftRecordRepository extends JpaRepository<MoldPreShiftRecord, Integer>,
    JpaSpecificationExecutor<MoldPreShiftRecord> {

}
