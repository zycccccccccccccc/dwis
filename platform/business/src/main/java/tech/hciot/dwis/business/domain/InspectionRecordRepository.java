package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.InspectionRecord;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Integer>,
    JpaSpecificationExecutor<InspectionRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM inspection_record WHERE wheel_serial = :wheelSerial", nativeQuery = true)
  Integer getMaxTs(@Param("wheelSerial") String wheelSerial);
}
