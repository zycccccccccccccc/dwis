package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.MachineRecord;

public interface MachineRecordRepository extends JpaRepository<MachineRecord, Integer>, JpaSpecificationExecutor<MachineRecord> {

  Optional<MachineRecord> findByWheelSerial(String wheelSerial);
}
