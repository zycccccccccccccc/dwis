package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.UtTestRecord;

public interface UtTestRecordRepository extends JpaRepository<UtTestRecord, Integer>, JpaSpecificationExecutor<UtTestRecord> {

  @Query(value = "SELECT MAX(ts) FROM ut_test_record " +
    "WHERE inspector_id = :inspectorId AND shift_no = :shiftNo AND ope_d_t BETWEEN :beginDate AND :endDate", nativeQuery = true)
  Optional<BigDecimal> findMaxTimes(@Param("inspectorId") String inspectorId,
                                    @Param("shiftNo") String shiftNo,
                                    @Param("beginDate") String beginDate,
                                    @Param("endDate") String endDate);
}
