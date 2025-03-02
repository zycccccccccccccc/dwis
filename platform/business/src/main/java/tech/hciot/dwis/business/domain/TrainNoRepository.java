package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.TrainNo;

public interface TrainNoRepository extends JpaRepository<TrainNo, Integer>,
    JpaSpecificationExecutor<TrainNo> {

  Optional<TrainNo> findByShippedNo(String shippedNo);

  void deleteByShippedNo(String hgz);

  @Query(value = "SELECT wheel_record.design AS 轮型,train_no.shipped_date AS 发运日期,wheel_record.wheel_serial AS 车轮序列号,"
      + "wheel_record.shelf_number AS 串号,wheel_record.balance_s,train_no.shipped_no AS 合格证号,train_no.shipped_id AS 发运员工号,"
      + "train_no.train_no AS 车皮号,customer.customer_name AS 收货单位 "
      + "FROM "
      + "customer "
      + "INNER JOIN train_no ON customer.customer_id = train_no.customer_id "
      + "INNER JOIN wheel_record ON train_no.shipped_no = wheel_record.shipped_no "
      + "WHERE "
      + "train_no.shipped_no = ?1 ORDER BY wheel_record.wheel_serial", nativeQuery = true)
  List<Object[]> getPrintData(String hgz);
}
