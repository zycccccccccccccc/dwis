package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.MagneticRecord;

public interface MagneticRecordRepository extends JpaRepository<MagneticRecord, Integer>, JpaSpecificationExecutor<MagneticRecord> {

  @Query(value = "SELECT MAX(ts) FROM magnetic_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Optional<BigDecimal> findMaxMagneticTimes(@Param("wheelSerial") String wheelSerial);
}
