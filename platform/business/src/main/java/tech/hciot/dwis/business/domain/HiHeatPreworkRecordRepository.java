package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.HiHeatPreworkRecord;

public interface HiHeatPreworkRecordRepository extends JpaRepository<HiHeatPreworkRecord, Integer>,
    JpaSpecificationExecutor<HiHeatPreworkRecord> {

}
