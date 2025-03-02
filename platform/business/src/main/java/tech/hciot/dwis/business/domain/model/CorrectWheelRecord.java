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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class CorrectWheelRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer logId;
  private String inspectorId;
  private String wheelSerial;
  private Integer recallType;
  private String holdCode;
  private String reworkCode;
  private String scrapCode;
  private String cihenCode;
  private String formerCheckCode;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date formerStockDate;
  private String formerShippedNo;
  private Integer confirmedScrap;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date scrapDate;
  private String memo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Column(name = "[ope_d_t]")
  private Date opeDT;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createDate;
}
