package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ReleaseRecord;

public interface ReleaseRecordRepository extends JpaRepository<ReleaseRecord, Integer>, JpaSpecificationExecutor<ReleaseRecord> {

  @Query(value = "SELECT top 1 * FROM release_record WHERE wheel_serial = :wheelSerial ORDER BY ope_d_t DESC",
    nativeQuery = true)
  Optional<ReleaseRecord> findNewestWheelSerial(@Param("wheelSerial") String wheelSerial);
}
