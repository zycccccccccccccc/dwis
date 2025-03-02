package tech.hciot.dwis.business.domain;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.ColdWheel;

public interface ColdWheelRepository extends JpaRepository<ColdWheel, Integer>, JpaSpecificationExecutor<ColdWheel> {


  @Query(value = "SELECT h.cast_date,h.tap_seq,p.pit_seq,p.pit_no FROM pit_records p,pour_record r,ladle_record l,heat_record h "
      + "where p.pit_seq = r.pit_seq AND l.id = r.ladle_id AND h.id = l.heat_record_id AND h.cast_date = CAST( ?1 AS DATE) "
      + "AND h.tap_seq = ?2 GROUP BY h.cast_date,h.tap_seq,p.pit_seq,p.pit_no ORDER BY p.pit_seq",
      countQuery = "SELECT count(*) FROM pit_records p,pour_record r,ladle_record l,heat_record h "
          + "where p.pit_seq = r.pit_seq AND l.id = r.ladle_id AND h.id = l.heat_record_id AND h.cast_date = CAST( ?1 AS DATE) "
          + "AND h.tap_seq = ?2 GROUP BY h.cast_date,h.tap_seq,p.pit_seq,p.pit_no ORDER BY p.pit_seq",
      nativeQuery = true)
  Page<Map<String, Object>> findBuckets(String castDate, Integer tapSeq, Pageable pageable);
}
