package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ScrapReasonCode;

public interface ScrapReasonCodeRepository extends JpaRepository<ScrapReasonCode, Integer>,
    JpaSpecificationExecutor<ScrapReasonCode> {

  @Query(value = "SELECT * FROM scrap_reason_code WHERE scrap_code = :scrapCode AND enabled = 1" +
      " AND (location LIKE %:location% OR location LIKE '%all')", nativeQuery = true)
  List<ScrapReasonCode> findByScrapCode(@Param("scrapCode") String scrapCode,
      @Param("location") String location);

  Optional<ScrapReasonCode> findByScrapReasonCode(String scrapReasonCode);
}
