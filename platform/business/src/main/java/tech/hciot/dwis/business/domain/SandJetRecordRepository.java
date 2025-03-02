package tech.hciot.dwis.business.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.SandJetRecord;


public interface SandJetRecordRepository extends JpaRepository<SandJetRecord, Integer>,
    JpaSpecificationExecutor<SandJetRecord> {

    @Query(value = "SELECT TOP 1 * FROM sand_jet_record WHERE graphite = :graphite AND status = :status ORDER BY id DESC", nativeQuery = true)
    Optional<SandJetRecord> findByGraphiteAndStatus(@Param("graphite") String graphite, @Param("status") Integer status);

    @Query(value = "WITH t AS (SELECT TOP 1 graphite FROM sand_jet_record WHERE line_no = :lineNo ORDER BY id DESC) " +
            "SELECT COUNT(s.graphite) FROM sand_jet_record s INNER JOIN t ON s.graphite = t.graphite WHERE s.line_no = :lineNo GROUP BY s.graphite", nativeQuery = true)
    Integer findGraphiteRows(@Param("lineNo") Integer lineNo);

    @Query(value = "WITH t1 AS (SELECT TOP 1 graphite FROM sand_jet_record WHERE line_no = :lineNo ORDER BY id DESC), " +
            "t2 AS (SELECT s.id, ROW_NUMBER() OVER (PARTITION BY s.graphite ORDER BY s.id DESC) rn FROM sand_jet_record s INNER JOIN t1 " +
            "ON s.graphite = t1.graphite WHERE s.line_no = :lineNo), " +
            "t3 AS (SELECT id FROM t2 WHERE rn = 2) " +
            "SELECT TOP 1 s.graphite FROM sand_jet_record s, t3 WHERE s.id > t3.id AND s.line_no = :lineNo ORDER BY s.id ASC", nativeQuery = true)
    String findNextGraphite(@Param("lineNo") Integer lineNo);

    @Query(value = "SELECT TOP 1 SUBSTRING(wheel_serial, 8, 3) AS wheelNo FROM sand_jet_record WHERE line_no = :lineNo " +
            "AND SUBSTRING(wheel_serial, 1, 2) = :year AND SUBSTRING(wheel_serial, 3, 2) = :month AND SUBSTRING(wheel_serial, 6, 2) = :day ORDER BY id DESC", nativeQuery = true)
    String findCurrentWheelNo(@Param("year") String year, @Param("month") String month, @Param("day") String day, @Param("lineNo") Integer lineNo);

    @Query(value = "SELECT TOP 1 * FROM sand_jet_record WHERE pre_shift_id = :preShiftId AND line_no = :lineNo ORDER BY id DESC", nativeQuery = true)
    Optional<SandJetRecord> findNewestByPreShiftIdAndLineNo(@Param("preShiftId") Integer preShiftId, @Param("lineNo") Integer lineNo);
}
