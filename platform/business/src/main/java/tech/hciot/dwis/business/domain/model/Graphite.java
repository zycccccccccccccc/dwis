package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Graphite {

  // 石墨号下拉框条件
  public static final String GRAPHITE_NO_PROCESS = "PROCESS"; // 加工
  public static final String GRAPHITE_NO_REWORK = "REWORK"; // 返修
  public static final String GRAPHITE_NO_MODIFY_GRAPHITE_NO = "MODIFY_GRAPHITE_NO"; // 修改石墨号
  public static final String GRAPHITE_NO_UP = "UP"; // 上线
  public static final String GRAPHITE_NO_DOWN = "DOWN"; // 下线
  public static final String GRAPHITE_NO_SCRAP = "SCRAP"; // 报废

  // 石墨状态
  public static final Integer STATUS_ACCEPTED = 0;
  public static final Integer STATUS_PROCESSED = 1;
  public static final Integer STATUS_UP = 2;
  public static final Integer STATUS_DOWN = 3;
  public static final Integer STATUS_MODIFY_GRAPHITE_NO = 4;
  public static final Integer STATUS_REWORK = 5;
  public static final Integer STATUS_SCRAP = 6;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String graphite;
  private String graphiteKey;
  private String design;
  private Integer cd;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date processDate;

  private Integer grId;

  @Builder.Default
  private Integer repairTimes = 0;

  private String scrapCode;
  private Integer height;

  @Builder.Default
  private Integer redesignTimes = 0;

  @Builder.Default
  private Integer upTimes = 0;

  @Builder.Default
  private Integer downTimes = 0;

  private Integer status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private String memo;
}
