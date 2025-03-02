package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ShipTemp;

public interface ShipTempRepository extends JpaRepository<ShipTemp, Integer>, JpaSpecificationExecutor<ShipTemp> {

  @Query(value = "SELECT ISNULL(MAX(serial_no),0) FROM ship_temp", nativeQuery = true)
  Integer getMaxSerialNo();

  List<ShipTemp> getByHgzOrderBySerialNoAsc(String hgz);

  List<ShipTemp> getByHgzOrderByHgzSerialNoAsc(String hgz);

  @Query(value = "SELECT ship_temp.hgz_serial_no,ship_temp.hgz,ship_temp.wheel_serial,wheel_record.design,wheel_record.stock_date,"
      + "wheel_record.bore_size,wheel_record.tape_size,wheel_record.wheel_w,wheel_record.balance_s,"
      + "CASE WHEN SUBSTRING(wheel_record.mec_serial,1,1)='P' THEN '放行' ELSE '扣留' END AS mec_serial,"
      + "CASE WHEN wheel_record.test_code = '' OR (wheel_record.test_code != '' AND test_code.code_type = 'Release') THEN '' ELSE wheel_record.test_code END AS test_code,"
      + "wheel_record.shipped_no,ship_temp.shelf_no,wheel_record.shelf_number,CASE WHEN wheel_record.design ='CJ33' THEN CASE "
      + "WHEN chemistry_detail.s>=0.030 OR chemistry_detail.p>=0.025 THEN 'NO' ELSE 'BNSF' END ELSE wheel_record.n_grind END AS flag,"
      + "wheel_record.finished,wheel_record.check_code,ship_temp.create_time "
      + "FROM wheel_record RIGHT JOIN ship_temp ON wheel_record.wheel_serial = ship_temp.wheel_serial "
      + "LEFT JOIN chemistry_detail ON wheel_record.ladle_id = chemistry_detail.ladle_id "
      + "LEFT JOIN test_code ON wheel_record.test_code = test_code.code "
      + "WHERE ship_temp.HGZ = :hgz ORDER BY ship_temp.serial_no", nativeQuery = true,
      countQuery = "SELECT COUNT(1) FROM wheel_record RIGHT JOIN ship_temp ON wheel_record.wheel_serial = ship_temp.wheel_serial "
          + "LEFT JOIN chemistry_detail ON wheel_record.ladle_id = chemistry_detail.ladle_id WHERE ship_temp.hgz = :hgz")
  Page<Map<String, Object>> getShipData(@Param("hgz") String hgz, Pageable pageable);

  void deleteByHgzAndHgzSerialNo(String hgz, Integer hgzSerialNo);

  Optional<ShipTemp> findByHgzAndHgzSerialNo(String hgz, Integer hgzSerialNo);

  Integer deleteByHgz(String hgz);

  @Query(value = "WITH t1 AS (SELECT wheel_serial FROM ship_temp GROUP BY wheel_serial HAVING(COUNT(wheel_serial) > 1)),"
      + "t2 AS (SELECT wheel_serial, RANK() OVER(ORDER BY id) AS hgz_SN FROM ship_temp WHERE hgz = :hgz),"
      + "t3 AS (SELECT s.wheel_serial, t2.hgz_SN FROM ship_temp s INNER JOIN t1 ON s.wheel_serial = t1.wheel_serial "
      + "INNER JOIN t2 ON s.wheel_serial = t2.wheel_serial WHERE s.hgz = :hgz)"
      + "SELECT wheel_serial, hgz_SN FROM t3 GROUP BY wheel_serial, hgz_SN ORDER BY hgz_SN", nativeQuery = true)
  List<Object> findRepeatedWheelSerialByHgz(@Param("hgz") String hgz);
}
