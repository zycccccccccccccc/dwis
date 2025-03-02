package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.FinalCheckRecord;

import java.util.Date;

public interface FinalCheckRecordRepository extends JpaRepository<FinalCheckRecord, Integer>, JpaSpecificationExecutor<FinalCheckRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM final_check_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Integer findMaxFinalCheckTimes(@Param("wheelSerial") String wheelSerial);

  @Query(value = "SELECT create_time FROM (SELECT create_time, ROW_NUMBER() OVER (PARTITION BY wheel_serial ORDER BY id ASC ) rn " +
          "FROM final_check_record WHERE wheel_serial = :wheelSerial)t WHERE rn = 1", nativeQuery = true)
  Date findFirstRecordTime(@Param("wheelSerial") String wheelSerial);
}
