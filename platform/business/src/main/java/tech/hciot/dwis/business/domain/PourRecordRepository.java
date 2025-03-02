package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.PourRecord;

public interface PourRecordRepository extends JpaRepository<PourRecord, Integer>, JpaSpecificationExecutor<PourRecord> {

  List<PourRecord> findByPitSeq(Integer pitSeq);

  int countByWheelSerial(String wheelSerial);

  @Query(value = "UPDATE pour_record SET scrap_code = '' WHERE wheel_serial = :wheelSerial", nativeQuery = true)
  @Modifying
  void clearScrapCode(@Param("wheelSerial") String wheelSerial);

  PourRecord findByWheelSerial(String wheelSerial);

  List<PourRecord> findByLadleId(Integer ladleId);

  int countByLadleId(Integer ladleId);
}
