package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.RawWheelPrintRecord;

public interface RawWheelPrintRecordRepository extends JpaRepository<RawWheelPrintRecord, Integer>, JpaSpecificationExecutor<RawWheelPrintRecord> {

  @Query(value = "SELECT MAX(ts) FROM raw_wheel_print_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Optional<BigDecimal> findMaxTimes(@Param("wheelSerial") String wheelSerial);
}
