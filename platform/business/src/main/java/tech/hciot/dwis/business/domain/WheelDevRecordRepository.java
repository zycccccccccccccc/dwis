package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.WheelDevRecord;

public interface WheelDevRecordRepository extends JpaRepository<WheelDevRecord, Integer>, JpaSpecificationExecutor<WheelDevRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM wheel_dev_record " +
    "WHERE inspector_id = :inspectorId AND wheel_serial = :wheelSerial", nativeQuery = true)
  Integer findByMaxTs(@Param("inspectorId") String inspectorId,
                      @Param("wheelSerial") String wheelSerial);
}
