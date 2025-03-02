package tech.hciot.dwis.business.domain;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.WMachineRecord;

public interface WMachineRecordRepository extends JpaRepository<WMachineRecord, Integer>, JpaSpecificationExecutor<WMachineRecord> {

  @Query(value = "SELECT COUNT(1) FROM w_machine_record " +
    "WHERE machine_no = :machineNo AND operator = :operator AND create_time >= :last12Hour", nativeQuery = true)
  Integer totalMachineCount(@Param("machineNo") Integer machineNo,
                            @Param("operator") String operator,
                            @Param("last12Hour") Date last12Hour);

  @Query(value = "SELECT DISTINCT machine_no FROM w_machine_record", nativeQuery = true)
  List<Integer> machineNoList();
}
