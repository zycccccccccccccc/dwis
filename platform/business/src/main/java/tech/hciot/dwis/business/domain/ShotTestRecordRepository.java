package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ShotTestRecord;

public interface ShotTestRecordRepository extends JpaRepository<ShotTestRecord, Integer>, JpaSpecificationExecutor<ShotTestRecord> {

  @Query(value = "SELECT MAX(ts) FROM shot_test_record " +
    "WHERE inspector_id = :inspectorId AND shift_no = :shiftNo AND ope_d_t BETWEEN :beginDate AND :endDate", nativeQuery = true)
  Optional<BigDecimal> findMaxTimes(@Param("inspectorId") String inspectorId,
                                    @Param("shiftNo") String shiftNo,
                                    @Param("beginDate") String beginDate,
                                    @Param("endDate") String endDate);
}
