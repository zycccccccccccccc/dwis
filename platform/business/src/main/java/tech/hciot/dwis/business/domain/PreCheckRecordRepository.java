package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.PreCheckRecord;

public interface PreCheckRecordRepository extends JpaRepository<PreCheckRecord, Integer>, JpaSpecificationExecutor<PreCheckRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM pre_check_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Integer findMaxPreCheckTimes(@Param("wheelSerial") String wheelSerial);
}
