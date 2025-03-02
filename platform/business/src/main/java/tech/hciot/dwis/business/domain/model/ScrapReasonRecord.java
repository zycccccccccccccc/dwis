package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.business.interfaces.dto.ScrapWheelDetail;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedNativeQuery(
    name = "ScrapWheelDetail",
    query = "SELECT wheel_record.wheel_serial AS wheelSerial,wheel_record.design,"
        + "wheel_record.scrap_code AS scrapCode,wheel_record.xray_req AS xrayReq,wheel_record.rework_code AS reworkCode,"
        + "wheel_record.test_code AS testCode,"
        + "SUBSTRING(wheel_record.mec_serial,1,LEN(wheel_record.mec_serial)-CHARINDEX('-',REVERSE(wheel_record.mec_serial))) AS "
        + "mecSerial,CASE wheel_record.test_code WHEN 'XN' THEN SUBSTRING(ladle_record.ladle_record_key, 1, LEN(ladle_record"
        + ".ladle_record_key)-CHARINDEX('_',REVERSE(ladle_record.ladle_record_key))) ELSE '' END AS heatKey "
        + "FROM wheel_record "
        + "INNER JOIN ladle_record ON wheel_record.ladle_id = ladle_record.id "
        + "WHERE wheel_record.scrap_code != '' AND wheel_record.confirmed_scrap = 0 "
        + "ORDER BY wheel_record.wheel_serial ASC",
    resultSetMapping = "ScrapWheelDetailMapping"
)
@SqlResultSetMapping(
    name = "ScrapWheelDetailMapping",
    classes = {
        @ConstructorResult(
            targetClass = ScrapWheelDetail.class,
            columns = {
                @ColumnResult(name = "wheelSerial", type = String.class),
                @ColumnResult(name = "design", type = String.class),
                @ColumnResult(name = "scrapCode", type = String.class),
                @ColumnResult(name = "xrayReq", type = Integer.class),
                @ColumnResult(name = "reworkCode", type = String.class),
                @ColumnResult(name = "testCode", type = String.class),
                @ColumnResult(name = "mecSerial", type = String.class),
                @ColumnResult(name = "heatKey", type = String.class)
            }
        )
    }
)
public class ScrapReasonRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String inspectorId;
  private String wheelSerial;
  private String design;
  private String scrapCode;
  private String scrapReasonCode;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
  @Transient
  private String scrapReason;
}
