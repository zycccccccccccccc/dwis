package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.BalanceTestRecord;

public interface BalanceTestRecordRepository extends JpaRepository<BalanceTestRecord, Integer>, JpaSpecificationExecutor<BalanceTestRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM balance_test_record " +
    "WHERE inspector_id = :inspectorId AND shift_no = :shiftNo AND ope_d_t BETWEEN :beginDate AND :endDate", nativeQuery = true)
  Integer findMaxTimes(@Param("inspectorId") String inspectorId,
                       @Param("shiftNo") String shiftNo,
                       @Param("beginDate") String beginDate,
                       @Param("endDate") String endDate);
}
