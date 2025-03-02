package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.GraphiteRecord;

public interface GraphiteRecordRepository extends JpaRepository<GraphiteRecord, Integer>, JpaSpecificationExecutor<GraphiteRecord> {

  @Query(value = "SELECT distinct top (:limit) graphite FROM graphite_record WHERE graphite LIKE :graphite% ORDER BY graphite", nativeQuery = true)
  List<String> findGraphiteList(@Param("graphite") String graphiteKey, @Param("limit") Integer limit);

}
