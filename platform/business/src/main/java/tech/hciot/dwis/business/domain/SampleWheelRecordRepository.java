package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.SampleWheelRecord;

public interface SampleWheelRecordRepository extends JpaRepository<SampleWheelRecord, Integer>,
    JpaSpecificationExecutor<SampleWheelRecord> {

  int countByWheelSerial(String wheelSerial);
}
