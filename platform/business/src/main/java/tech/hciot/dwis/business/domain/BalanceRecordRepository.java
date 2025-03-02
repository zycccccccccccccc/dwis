package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.BalanceRecord;

public interface BalanceRecordRepository extends JpaRepository<BalanceRecord, Integer>, JpaSpecificationExecutor<BalanceRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM balance_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Integer findMaxBalanceTimes(@Param("wheelSerial") String wheelSerial);
}
