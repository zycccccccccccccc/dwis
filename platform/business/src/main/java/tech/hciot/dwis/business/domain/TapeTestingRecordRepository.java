package tech.hciot.dwis.business.domain;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TapeTestingRecord;

public interface TapeTestingRecordRepository extends JpaRepository<TapeTestingRecord, Integer>, JpaSpecificationExecutor<TapeTestingRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM tape_testing_record " +
    "WHERE inspector_id = :inspectorId AND shift_no = :shiftNo AND test_date = :testDate", nativeQuery = true)
  Integer findByMaxTs(@Param("inspectorId") String inspectorId,
                      @Param("shiftNo") String shiftNo,
                      @Param("testDate") Date testDate);
}
