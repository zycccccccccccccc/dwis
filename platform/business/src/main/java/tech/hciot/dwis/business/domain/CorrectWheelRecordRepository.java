package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.CorrectWheelRecord;

public interface CorrectWheelRecordRepository extends JpaRepository<CorrectWheelRecord, Integer>,
    JpaSpecificationExecutor<CorrectWheelRecord> {

}
