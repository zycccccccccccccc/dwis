package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.LowHeatPreworkRecord;

public interface LowHeatPreworkRecordRepository extends JpaRepository<LowHeatPreworkRecord, Integer>,
    JpaSpecificationExecutor<LowHeatPreworkRecord> {

}
