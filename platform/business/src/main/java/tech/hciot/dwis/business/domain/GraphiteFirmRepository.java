package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.GraphiteFirm;

public interface GraphiteFirmRepository extends JpaRepository<GraphiteFirm, Integer>, JpaSpecificationExecutor<GraphiteFirm> {

  Optional<GraphiteFirm> findByGraphiteKey(String graphiteKey);

  @Query(value = "SELECT * FROM v_max_graphite_firm ORDER BY graphitePre", nativeQuery = true)
  Page<Map<String, Object>> findMaxGraphite(Pageable pageable);

  @Query(value = "SELECT top (:limit) graphite_key FROM graphite_firm WHERE status = 0 AND graphite_key LIKE :graphiteKey% ORDER BY graphite_key", nativeQuery = true)
  List<String> findGraphiteFirmList(@Param("graphiteKey") String graphiteKey, @Param("limit") Integer limit);
}
