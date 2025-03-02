package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.BarcodePrintRecord;

public interface BarcodePrintRecordRepository extends JpaRepository<BarcodePrintRecord, Integer>, JpaSpecificationExecutor<BarcodePrintRecord> {

  @Query(value = "SELECT ISNULL(MAX(ts), 0) FROM barcode_print_record WHERE wheel_serial = :wheelSerial ", nativeQuery = true)
  Integer findMaxPrintTimes(@Param("wheelSerial") String wheelSerial);
}
