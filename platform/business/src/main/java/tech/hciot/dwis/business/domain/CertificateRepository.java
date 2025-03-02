package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Integer>, JpaSpecificationExecutor<Certificate> {

//    @Query(value = "SELECT * FROM v_certificate WHERE type_kxsj = :shippedNo ORDER BY wheel_serial", nativeQuery = true)
    List<Certificate> findByShippedNoOrderByWheelSerial(@Param("shippedNo") String shippedNo);
}
