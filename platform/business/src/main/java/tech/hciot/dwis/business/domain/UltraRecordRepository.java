package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.UltraRecord;

public interface UltraRecordRepository extends JpaRepository<UltraRecord, Integer>, JpaSpecificationExecutor<UltraRecord> {

  @Query(value = "SELECT MAX(ts) FROM ultra_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Optional<BigDecimal> findMaxUltraTimes(@Param("wheelSerial") String wheelSerial);

  @Query(value = "WITH t1 AS (SELECT l.heat_record_id FROM wheel_record w INNER JOIN ladle_record l ON w.ladle_id = l.id WHERE w.wheel_serial = :wheelSerial) " +
          "SELECT SUM (CASE WHEN w.scrap_code= '8CS' OR w.rework_code = '8C' THEN 1 ELSE 0 END) AS sum_8CS FROM wheel_record w INNER JOIN ladle_record l ON w.ladle_id = l.id " +
          "INNER JOIN t1 ON t1.heat_record_id = l.heat_record_id GROUP BY l.heat_record_id", nativeQuery = true)
  Integer find8CSNumByWheelSerial(@Param("wheelSerial") String wheelSerial);
}
