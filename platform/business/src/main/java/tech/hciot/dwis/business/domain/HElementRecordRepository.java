package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.HElementRecord;

public interface HElementRecordRepository extends JpaRepository<HElementRecord, Integer>,
    JpaSpecificationExecutor<HElementRecord> {

}
