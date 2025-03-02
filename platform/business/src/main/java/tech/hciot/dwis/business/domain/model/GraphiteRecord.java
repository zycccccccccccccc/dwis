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
public class GraphiteRecord {

  public static final Integer STATUS_ACCEPTED = 0;
  public static final Integer STATUS_PROCESSED = 1;
  public static final Integer STATUS_ONLINE = 2;
  public static final Integer STATUS_OFFLINE = 3;
  public static final Integer STATUS_MODIFY_GRAPHITE_NO = 4;
  public static final Integer STATUS_REWORK = 5;
  public static final Integer STATUS_SCRAP = 6;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String graphiteKey;
  private String graphite;
  private String reGraphite;
  private String design;
  private String reDesign;
  private Integer cd;
  private String graphiteOpeId;
  private String processId;
  private Integer height;

  @Builder.Default
  private Integer processSize = 0;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date processDate;

  private String scrapCode;
  private String reworkCode;
  private Integer status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private String memo;
}
