package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.business.interfaces.dto.ShipCheckCodeResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@NamedNativeQuery(
    name = "ShipCheckCodeResponse",
    query = "SELECT COUNT(wheel_record.wheel_serial) AS amount,wheel_record.design,"
        + "wheel_record.bore_size AS boreSize,wheel_record.tape_size AS tapSize,wheel_record.wheel_w AS wheelW,"
        + "Wheel_record.balance_s AS balanceS,ship_temp.hgz,"
        + "CASE WHEN SUBSTRING(wheel_record.mec_serial,1,1) = 'P' THEN '放行' ELSE '扣留' END AS mecSerial, "
        + "CASE WHEN wheel_record.test_code = '' OR (wheel_record.test_code != '' AND test_code.code_type = 'Release') THEN '' ELSE wheel_record.test_code END AS testCode "
        + "FROM ship_temp "
        + "INNER JOIN wheel_record ON ship_temp.wheel_serial = wheel_record.wheel_serial "
        + "LEFT JOIN test_code ON wheel_record.test_code = test_code.code "
        + "WHERE wheel_record.finished = 1 AND wheel_record.confirmed_scrap = 0 AND wheel_record.shipped_no IS NULL "
        + "AND wheel_record.stock_date IS NOT NULL AND LEFT(wheel_record.check_code,1) != 'A' "
        + "GROUP BY wheel_record.design,wheel_record.bore_size,wheel_record.tape_size,wheel_record.wheel_w,"
        + "wheel_record.balance_s,ship_temp.hgz,"
        + "CASE WHEN SUBSTRING(wheel_record.mec_serial,1,1) = 'P' THEN '放行' ELSE '扣留' END, "
        + "CASE WHEN wheel_record.test_code = '' OR (wheel_record.test_code != '' AND test_code.code_type = 'Release') THEN '' ELSE wheel_record.test_code END "
        + "HAVING ship_temp.hgz = ?1 "
        + "ORDER BY wheel_record.tape_size",
    resultSetMapping = "ShipCheckCodeResponseMapping"
)
@NamedNativeQuery(
    name = "WheelResponse",
    query = "SELECT COUNT(wheel_record.wheel_serial) AS amount,wheel_record.design,"
        + "wheel_record.bore_size AS boreSize,wheel_record.tape_size AS tapSize,wheel_record.wheel_w AS wheelW,"
        + "Wheel_record.balance_s AS balanceS,ship_temp.hgz,'' AS mecSerial "
        + "FROM ship_temp "
        + "INNER JOIN wheel_record ON ship_temp.wheel_serial = wheel_record.wheel_serial "
        + "WHERE wheel_record.finished = 1 AND wheel_record.confirmed_scrap = 0 AND wheel_record.shipped_no IS NULL "
        + "AND wheel_record.stock_date IS NOT NULL AND LEFT(wheel_record.check_code,1) != 'A' AND wheel_record.n_grind= 'NG' "
        + "GROUP BY wheel_record.design,wheel_record.bore_size,wheel_record.tape_size,wheel_record.wheel_w,"
        + "wheel_record.balance_s,ship_temp.hgz "
        + "HAVING ship_temp.hgz = ?1 "
        + "ORDER BY wheel_record.tape_size",
    resultSetMapping = "ShipCheckCodeResponseMapping"
)
@SqlResultSetMapping(
    name = "ShipCheckCodeResponseMapping",
    classes = {
        @ConstructorResult(
            targetClass = ShipCheckCodeResponse.class,
            columns = {
                @ColumnResult(name = "amount", type = Integer.class),
                @ColumnResult(name = "design", type = String.class),
                @ColumnResult(name = "boreSize", type = Integer.class),
                @ColumnResult(name = "tapSize", type = BigDecimal.class),
                @ColumnResult(name = "wheelW", type = Integer.class),
                @ColumnResult(name = "balanceS", type = String.class),
                @ColumnResult(name = "hgz", type = String.class),
                @ColumnResult(name = "mecSerial", type = String.class),
                @ColumnResult(name = "testCode", type = String.class)
            }
        )
    }
)
public class ShipTemp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String pId;
  private String hgz;
  private String wheelSerial;
  private Integer serialNo;
  private Integer hgzSerialNo;
  private String shelfNo;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
