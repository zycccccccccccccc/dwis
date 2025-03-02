package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ThreehbRecord;

public interface ThreehbRecordRepository extends JpaRepository<ThreehbRecord, Integer>, JpaSpecificationExecutor<ThreehbRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM threehb_record " +
    "WHERE inspector_id = :inspectorId AND wheel_serial = :wheelSerial", nativeQuery = true)
  Integer findByMaxTs(@Param("inspectorId") String inspectorId,
                      @Param("wheelSerial") String wheelSerial);
}
