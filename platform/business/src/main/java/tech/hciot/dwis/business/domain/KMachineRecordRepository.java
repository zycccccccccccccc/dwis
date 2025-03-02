package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.KMachineRecord;

import java.util.Date;
import java.util.List;

public interface KMachineRecordRepository extends JpaRepository<KMachineRecord, Integer>, JpaSpecificationExecutor<KMachineRecord> {

  @Query(value = "SELECT location, COUNT(1) cnt FROM k_machine_record " +
    "WHERE machine_no = :machineNo AND operator = :operator AND create_time >= :last12Hour " +
    "GROUP BY location", nativeQuery = true)
  List<Object[]> totalMachineCount(@Param("machineNo") Integer machineNo,
                                   @Param("operator") String operator,
                                   @Param("last12Hour") Date last12Hour);

  @Query(value = "SELECT DISTINCT machine_no FROM k_machine_record", nativeQuery = true)
  List<Integer> machineNoList();


  @Query(value = "SELECT COUNT(wheel_serial) FROM k_machine_record WHERE wheel_serial = :wheelSerial AND k_s2 = :kS2 GROUP BY wheel_serial", nativeQuery = true)
  Integer findKCountsByWheelSerial(@Param("wheelSerial") String wheelSerial, @Param("kS2") Integer kS2);
}
