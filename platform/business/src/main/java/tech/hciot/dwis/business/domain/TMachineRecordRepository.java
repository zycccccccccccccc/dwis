package tech.hciot.dwis.business.domain;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TMachineRecord;

public interface TMachineRecordRepository extends JpaRepository<TMachineRecord, Integer>, JpaSpecificationExecutor<TMachineRecord> {

  @Query(value = "SELECT COUNT(1) FROM t_machine_record " +
    "WHERE machine_no = :machineNo AND operator = :operator AND create_time >= :last12Hour AND t_s2 <> 8", nativeQuery = true)
  Integer totalMachineCount(@Param("machineNo") Integer machineNo,
                            @Param("operator") String operator,
                            @Param("last12Hour") Date last12Hour);

  @Query(value = "SELECT DISTINCT machine_no FROM t_machine_record", nativeQuery = true)
  List<Integer> machineNoList();

  @Query(value = "SELECT create_time FROM (SELECT create_time, ROW_NUMBER() OVER (PARTITION BY wheel_serial ORDER BY id ASC ) rn " +
    "FROM t_machine_record WHERE t_s2 = :s2 AND wheel_serial = :wheelSerial)t WHERE rn = 1", nativeQuery = true)
  Date findFirst51RecordTime(@Param("wheelSerial") String wheelSerial,
                              @Param("s2") Integer s2);
}
