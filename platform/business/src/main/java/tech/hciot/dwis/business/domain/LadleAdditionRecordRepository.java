package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.LadleAdditionRecord;

import java.util.List;

public interface LadleAdditionRecordRepository extends JpaRepository<LadleAdditionRecord, Integer>,
    JpaSpecificationExecutor<LadleAdditionRecord> {
    @Query(value = "SELECT * FROM ladle_addition_record WHERE heat_record_id = :heatRecordId " +
            "AND ladle_seq = :ladleSeq", nativeQuery = true)
    LadleAdditionRecord findByHeatRecordIdAndLaddleSeq(@Param("heatRecordId") Integer heatRecordId,
                                       @Param("ladleSeq") Integer ladleSeq);

    @Query(value = "SELECT TOP 1 l.heat_record_id, h.furnace_no, h.heat_seq, l.ladle_seq AS ladle_seq FROM ladle_addition_record l INNER JOIN heat_record h " +
            " ON l.heat_record_id = h.id WHERE l.pourdirect_id IS NOT NULL AND l.pourdirect_time IS NOT NULL " +
            " AND l.pourdirect_time > DATEADD(HOUR, -24, GETDATE()) ORDER BY l.pourdirect_time DESC", nativeQuery = true)
    Object findIdsNewest();

    @Query(value = "SELECT * FROM ladle_addition_record WHERE heat_record_id = :heatRecordId ORDER BY id ASC", nativeQuery = true)
    List<LadleAdditionRecord> findByHeatRecordId(Integer heatRecordId);
}
