package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TransportRecord;

public interface TransportRecordRepository extends JpaRepository<TransportRecord, Integer>,
    JpaSpecificationExecutor<TransportRecord> {

  @Query(value = "SELECT MAX(ts) FROM transport_record WHERE wheel_serial = :wheelSerial AND ope_type = :opeType", nativeQuery = true)
  Optional<BigDecimal> findMaxTransportTimes(@Param("wheelSerial") String wheelSerial, @Param("opeType") Integer opeType);
}
