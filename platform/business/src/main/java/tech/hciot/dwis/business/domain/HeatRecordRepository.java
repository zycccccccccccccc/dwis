package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.HeatRecord;

public interface HeatRecordRepository extends JpaRepository<HeatRecord, Integer>, JpaSpecificationExecutor<HeatRecord> {

  @Query(value = "SELECT ISNULL(MAX(heat_seq), 0) FROM heat_record WHERE furnace_no = :furnaceNo " +
      "AND cast_date BETWEEN :beginDate AND :endDate", nativeQuery = true)
  Integer findMaxSeq(@Param("furnaceNo") Integer furnaceNo,
      @Param("beginDate") String beginDate,
      @Param("endDate") String endDate);

  @Query(value = "SELECT id FROM heat_record r WHERE r.id = (SELECT max(id) FROM heat_record WHERE id < :id)", nativeQuery = true)
  Optional<Integer> findLastHeatRecordId(@Param("id") Integer id);

  Optional<HeatRecord> findByHeatRecordKey(String heatRecordKey);

  @Query(value = "SELECT top (1) id FROM heat_record WHERE furnace_no = :furnaceNo AND heat_seq = :heatSeq " +
      "AND record_created > :createTime", nativeQuery = true)
  Optional<Integer> findHeatRecordIdForLab(@Param("furnaceNo") Integer furnaceNo,
      @Param("heatSeq") Integer heatSeq,
      @Param("createTime") String createTime);

  @Query(value = "SELECT heat_record.id AS heat_record_id,heat_record.furnace_no,heat_record.heat_seq,heat_record.tap_seq "
      + "FROM heat_record INNER JOIN ladle_record ON heat_record.id = ladle_record.heat_record_id "
      + "INNER JOIN wheel_record ON wheel_record.ladle_id = ladle_record.id "
      + "WHERE wheel_record.design LIKE 'SA34%' AND heat_record.cast_date = :date "
      + "GROUP BY heat_record.id, heat_record.furnace_no, heat_record.heat_seq, heat_record.tap_seq "
      + "ORDER BY heat_record.tap_seq", nativeQuery = true)
  List<Map<String, Object>> findHElementHeatRecord(@Param("date") String date);

  @Query(value = "SELECT DISTINCT tap_seq FROM heat_record ORDER BY tap_seq", nativeQuery = true)
  List<String> findTapSeqList();

  @Query(value = "SELECT DISTINCT tap_seq FROM heat_record WHERE cast_date = :castDate ORDER BY tap_seq", nativeQuery = true)
  List<Integer> findTapSeqListByCastDate(@Param("castDate") String castDate);
}
