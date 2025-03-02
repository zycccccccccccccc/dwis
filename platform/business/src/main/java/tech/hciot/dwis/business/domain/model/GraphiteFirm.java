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
public class GraphiteFirm {

  public static final Integer STATUS_UNPROCESS = 0;
  public static final Integer STATUS_PROCESSED = 1;
  public static final Integer STATUS_SCRAP = 6;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String graphiteKey;
  private Integer diameter;
  private String graphiteOpeId;
  private Integer manufacturerId;
  private String manufacturerName;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date receiveDate;
  private String scrapCode;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date scrapDate;
  private Integer status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private String memo;
}
