package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.CihenRecordPre;

public interface CihenRecordPreRepository extends JpaRepository<CihenRecordPre, Integer>,
    JpaSpecificationExecutor<CihenRecordPre> {

  @Query(value = "SELECT MAX(ts) FROM cihen_record_pre WHERE wheel_serial = :wheelSerial", nativeQuery = true)
  Integer getMaxTs(@Param("wheelSerial") String wheelSerial);
}
