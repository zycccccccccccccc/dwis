package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.FinalSprayRecord;

import java.util.Optional;


public interface FinalSprayRecordRepository extends JpaRepository<FinalSprayRecord, Integer>,
    JpaSpecificationExecutor<FinalSprayRecord> {

    @Query(value = "SELECT TOP 1 * FROM final_spray_record WHERE pre_shift_id = :preShiftId ORDER BY id DESC", nativeQuery = true)
    Optional<FinalSprayRecord> findNewestByPreShiftId(@Param("preShiftId") Integer preShiftId);
}
