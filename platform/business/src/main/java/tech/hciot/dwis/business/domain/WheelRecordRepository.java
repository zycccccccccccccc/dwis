package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.OutfitCheckResponse;

public interface WheelRecordRepository extends JpaRepository<WheelRecord, Integer>, JpaSpecificationExecutor<WheelRecord> {

  int countByWheelSerial(String wheelSerial);

  @Query(value = "SELECT top (:limit) wheel_serial FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "ORDER BY wheel_serial", nativeQuery = true)
  List<String> findWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  Optional<WheelRecord> findByWheelSerial(String wheelSerial);

  @Query(value = "SELECT top (:limit) wheel_serial FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND pre > 0 AND confirmed_scrap = 0 AND finished = 0 AND scrap_code = '' AND hfs = '0' ORDER BY wheel_serial", nativeQuery = true)
  List<String> findRawWheelPrintWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 0 AND finished = 0 AND rework_code != '' AND scrap_code = '' ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findReleaseWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 0 AND finished = 0 ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findNotFinishNotScrapWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 0 AND finished = 0 AND scrap_code != '' ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findNeedScrapWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 1 ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findScrapWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 1 AND finished = 0 AND scrap_code != '' ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findCorrectScrapWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND finished = 1 AND stock_date IS NULL ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findCorrectFinishWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND finished = 1 AND stock_date IS NOT NULL AND shipped_no IS NULL ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findCorrectStockWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND finished = 1 AND stock_date IS NOT NULL AND shipped_no IS NOT NULL ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findCorrectReturnWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND finished = 0 AND xray_req = 1 AND confirmed_scrap = 0 ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findXRayWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND finished = 0 AND final > 0 AND confirmed_scrap = 0 AND (design = 'CJ33' OR design = 'CP33') ORDER BY wheel_serial",
      nativeQuery = true)
  List<WheelRecord> findCihenWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 0 AND finished = 0 AND final > 0 ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findUtTestWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) * FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 1 AND SUBSTRING(mec_serial,1,1) = 'P' ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findPerformanceWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) wheel_serial FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
          "AND scrap_code ='HLD' AND confirmed_scrap = 0 AND finished = 0 ORDER BY wheel_serial", nativeQuery = true)
  List<String> findHLDReinspWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) wheel_serial FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND pre > 0 AND confirmed_scrap = 0 AND finished = 0 ORDER BY wheel_serial", nativeQuery = true)
  List<String> findJMachineWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value =
      "SELECT top (:limit) w.wheel_serial FROM wheel_record w JOIN machine_record m ON w.wheel_serial = m.wheel_serial "
          + "WHERE w.wheel_serial LIKE :wheelSerial% AND w.pre > 0 "
          + "AND w.confirmed_scrap = 0 AND w.finished = 0 AND m.j_counts > 0 ORDER BY w.wheel_serial", nativeQuery = true)
  List<String> findTWMachineWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value =
      "SELECT top (:limit) w.wheel_serial FROM wheel_record w JOIN machine_record m ON w.wheel_serial = m.wheel_serial "
          + "WHERE w.wheel_serial LIKE :wheelSerial% AND w.pre > 0 "
          + "AND w.confirmed_scrap = 0 AND w.finished = 0 AND m.t_counts > 0 ORDER BY w.wheel_serial", nativeQuery = true)
  List<String> findKMachineWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) wheel_serial FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND confirmed_scrap = 0 AND finished = 0 AND hold_code IN ('Q1', 'Q2') ORDER BY wheel_serial", nativeQuery = true)
  List<String> findQMachineWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);


  @Query(value = "SELECT * FROM wheel_record WHERE wheel_serial = :wheelSerial " +
      "AND pre > 0 AND confirmed_scrap = 0 AND finished = 0 AND scrap_code = '' AND hfs = '0'", nativeQuery = true)
  Optional<WheelRecord> findRawWheelPrintWheel(@Param("wheelSerial") String wheelSerial);

  @Query(value = "SELECT wheel_record.wheel_serial,w_machine_record.machine_no,wheel_record.design FROM wheel_record JOIN "
      + "machine_record ON wheel_record.wheel_serial = machine_record.wheel_serial LEFT JOIN w_machine_record ON "
      + "w_machine_record.id = machine_record.w_id "
      + "WHERE wheel_record.last_balance >= :start AND wheel_record.last_balance <= :end AND wheel_record.design = :design AND "
      + "wheel_record.finished = 1 AND wheel_record.stock_date IS NULL AND wheel_record.shipped_no IS NULL "
      + "AND wheel_record.confirmed_scrap = 0",
      nativeQuery = true)
  List<OutfitCheckResponse> getOutfit(@Param("start") String startTime, @Param("end") String endTime,
      @Param("design") String design);

  @Query(value = "SELECT top (:limit) wheel_serial FROM wheel_record WHERE wheel_serial LIKE :wheelSerial% " +
      "AND finished = 1 ORDER BY wheel_serial", nativeQuery = true)
  List<String> findSampleWheelSerialList(String wheelSerial, Integer limit);

  @Query(value = "WITH t1 AS ( "
          + "SELECT w.wheel_serial FROM wheel_record w "
          + "LEFT JOIN hold_code hold ON hold.code = w.hold_code "
          + "LEFT JOIN heat_code heat ON heat.code = w.heat_code "
          + "LEFT JOIN test_code test ON test.code = w.test_code "
          + "INNER JOIN design ON design.design = w.design "
          + "WHERE (w.finished = 0 AND w.confirmed_scrap = 0 AND w.scrap_code = '' AND w.rework_code = '' AND w.balance > 0) "
          + "AND ((design.balance_check = 1 AND w.balance_v <= 125) OR (design.balance_check != 1)) "
          + "AND (hold.code_type = 'Release' or w.hold_code = '') "
          + "AND (heat.code_type = 'Release' or w.heat_code = '') "
          + "AND (test.code_type = 'Release' or w.test_code = '') "
          + ") "
          + "SELECT top(:limit) wheel_serial "
          + "FROM t1 WHERE wheel_serial LIKE :wheelSerial% "
          + "ORDER BY wheel_serial", nativeQuery = true)
  List<String> findXRayTransportWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) w.wheel_serial FROM wheel_record w "
      + "LEFT JOIN machine_record m ON m.wheel_serial = w.wheel_serial "
      + "LEFT JOIN heat_code heat on heat.code = w.heat_code "
      + "LEFT JOIN test_code test on test.code = w.test_code "
      + "WHERE w.wheel_serial LIKE :wheelSerial% AND w.finished = 0 AND w.confirmed_scrap = 0 AND w.rework_code = '' "
      + "AND w.scrap_code = '' AND w.balance > 0 AND w.hold_code = 'Q1' "
      + "AND ((w.design = 'HFZ915' AND (w.balance_v >= 125 AND w.balance_v <= 240)) OR (w.design != 'HFZ915' AND (w.balance_v >= 125 AND w.balance_v <= 220)))  "
      + "AND (heat.code_type = 'Release' or w.heat_code = '') "
      + "AND (test.code_type = 'Release' or w.test_code = '') "
      + "AND m.q_id IS NOT NULL "
      + "ORDER BY w.wheel_serial", nativeQuery = true)
  List<String> findDeWeightWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "SELECT top (:limit) w.wheel_serial "
      + "FROM wheel_record w "
      + "INNER JOIN machine_record m ON m.wheel_serial = w.wheel_serial "
      + "INNER JOIN k_machine_record k ON m.k_id_last = k.id "
      + "LEFT JOIN hold_code hold on hold.code = w.hold_code "
      + "LEFT JOIN heat_code heat on heat.code = w.heat_code "
      + "LEFT JOIN test_code test on test.code = w.test_code "
      + "WHERE w.wheel_serial LIKE :wheelSerial% AND w.finished = 0 AND w.confirmed_scrap = 0 AND w.rework_code = 'H7' "
      + "AND w.scrap_code = '' AND w.balance > 0 "
      + "AND (k.k_s2 = 206 OR k.k_s2 = 203 OR k.k_s2 = 187) "
      + "AND (w.balance_v >= 0 AND w.balance_v <= 220)  "
      + "AND (hold.code_type = 'Release' or w.hold_code = '') "
      + "AND (heat.code_type = 'Release' or w.heat_code = '') "
      + "AND (test.code_type = 'Release' or w.test_code = '') "
      + "AND k.machine_no = 23 "
      + "ORDER BY w.wheel_serial", nativeQuery = true)
  List<String> findBoreWheelSerialList(@Param("wheelSerial") String wheelSerial, @Param("limit") Integer limit);

  @Query(value = "UPDATE wheel_record SET scrap_code = '' WHERE wheel_serial = :wheelSerial", nativeQuery = true)
  @Modifying
  void clearScrapCode(@Param("wheelSerial") String wheelSerial);

  @Query(value = "SELECT * FROM wheel_record WHERE shelf_number = :shelfNumber ORDER BY wheel_serial", nativeQuery = true)
  List<WheelRecord> findWheelSerialByShelfNumber(@Param("shelfNumber") String shelfNumber);

  int countByShippedNo(String shippedNo);

  @Query(value = "SELECT wheel_record.wheel_serial,ship_temp.shelf_no from wheel_record "
          + "INNER JOIN ship_temp ON ship_temp.wheel_serial = wheel_record.wheel_serial "
          + "INNER JOIN design ON wheel_record.design = design.design "
          + "WHERE wheel_record.shipped_no IS NULL "
          + "AND wheel_record.stock_date IS NOT NULL "
          + "AND wheel_record.finished= 1 "
          + "AND ship_temp.hgz = :hgz "
          + "AND LEFT(wheel_record.check_code,1) != 'A' "
          + "AND SUBSTRING(wheel_record.mec_serial,1,1) LIKE CASE WHEN design.internal = 1  THEN 'P' WHEN design.internal != 1 THEN '_' END", nativeQuery = true)
  List<Object[]> findTransportWheel(@Param("hgz") String hgz);

  @Query(value = "UPDATE wheel_record SET shipped_no = NULL,shelf_number = NULL WHERE shipped_no = :hgz", nativeQuery = true)
  @Modifying
  Integer correctWheel(@Param("hgz") String hgz);

  int deleteByWheelSerial(String wheelSerial);

  @Query(value = "SELECT DISTINCT mec_serial FROM wheel_record WHERE confirmed_scrap = 1 AND SUBSTRING(mec_serial,1,1) = 'C' "
      + "ORDER BY mec_serial", nativeQuery = true)
  List<String> findMecSerialForCheck();

  @Query(value = "SELECT DISTINCT mec_serial FROM wheel_record WHERE mec_confirm = 1 AND SUBSTRING(mec_serial,1,1) = 'T' "
      + "ORDER BY mec_serial", nativeQuery = true)
  List<String> findMecSerialForCorrectANDRelease();

  @Query(value = "SELECT wheel_serial FROM wheel_record WHERE test_code = 'XN' "
          + "ORDER BY wheel_serial", nativeQuery = true)
  List<String> findXNForRelease();

  @Query(value = "UPDATE wheel_record SET mec_serial = :flag + SUBSTRING(mec_serial,2,LEN(mec_serial)) WHERE mec_serial LIKE "
      + ":mecSerial%", nativeQuery = true)
  @Modifying
  void updateMecSerial(@Param("flag") String flag, @Param("mecSerial") String mecSerial);

 @Query(value = "UPDATE wheel_record SET test_code = 'XNF' WHERE ladle_id = :ladleId", nativeQuery = true)
  @Modifying
  void updateXNTestCode(@Param("ladleId") Integer ladleId);

  @Query(value = "SELECT wheel_serial FROM wheel_record WHERE mec_serial LIKE :mecSerial%", nativeQuery = true)
  List<String> findWheelSerialByMecSerial(@Param("mecSerial") String mecSerial);

  @Query(value = "SELECT COUNT(1) from wheel_record "
          + "INNER JOIN ship_temp ON ship_temp.wheel_serial = wheel_record.wheel_serial "
          + "INNER JOIN design ON wheel_record.design = design.design "
          + "LEFT JOIN test_code ON wheel_record.test_code = test_code.code "
          + "WHERE wheel_record.shipped_no IS NULL "
          + "AND wheel_record.stock_date IS NOT NULL "
          + "AND wheel_record.finished= 1 "
          + "AND ship_temp.hgz = :hgz "
          + "AND LEFT(wheel_record.check_code,1) != 'A' "
          + "AND design.internal = 1 "
          + "AND (wheel_record.mec_serial IS NULL OR SUBSTRING(wheel_record.mec_serial,1,1) = 'C' "
          + "OR SUBSTRING(wheel_record.mec_serial,1,1) = 'T' OR (wheel_record.test_code != '' AND test_code.code_type = 'Hold'))", nativeQuery = true)
  int countByHgzAndMecSerial(@Param("hgz") String hgz);

  @Query(value = "SELECT heat_times FROM wheel_record WHERE wheel_serial = :wheelSerial", nativeQuery = true)
  Integer findHeatTimes(@Param("wheelSerial") String wheelSerial);

  @Query(value = "WITH t1 AS(SELECT l.heat_record_id FROM wheel_record w INNER JOIN ladle_record l ON w.ladle_id = l.id WHERE w.wheel_serial = :wheelSerial) " +
          "SELECT w.* FROM wheel_record w INNER JOIN ladle_record l ON w.ladle_id = l.id INNER JOIN t1 ON t1.heat_record_id = l.heat_record_id " +
          "WHERE w.finished = 1 AND (w.shipped_no IS NULL OR w.shipped_no = '')", nativeQuery = true)
  List<WheelRecord> findFinishedAndNotShippedByWheelSerial(@Param("wheelSerial") String wheelSerial);
}
