package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class InspectionRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer logId;
  private String inspectorId;
  private String leaderId;
  private String wheelSerial;
  private String reworkCode;
  private String holdCode;
  private String testCode;
  private String scrapCode;
  private String heatCode;
  @Default
  private Integer brinReq = 0;
  @Default
  private Integer specialUltra = 0;
  @Default
  private Integer xrayReq = 0;
  private String xrayResult;
  private String scrapResult;
  @Default
  private Integer specialMt = 0;
  private Integer ts;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Column(name = "[ope_d_t]")
  private Date opeDT;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createDate;
}
