package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.CihenRecord;

public interface CihenRecordRepository extends JpaRepository<CihenRecord, Integer>, JpaSpecificationExecutor<CihenRecord> {

  @Query(value = "SELECT MAX(ts) FROM cihen_record WHERE wheel_serial = :wheelSerial", nativeQuery = true)
  Integer getMaxTs(@Param("wheelSerial") String wheelSerial);
}
