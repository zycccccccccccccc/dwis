package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LadleAdditionRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer heatRecordId;
  private Integer ladleSeq;
  private BigDecimal cCal;
  private BigDecimal siCal;
  private BigDecimal mnCal;
  private BigDecimal alCal;
  private BigDecimal cAct;
  private BigDecimal siAct;
  private BigDecimal mnAct;
  private BigDecimal alAct;
  private BigDecimal cCon;
  private BigDecimal siCon;
  private BigDecimal mnCon;
  private String pourdirectId;
  private String materialpreId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date pourdirectTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date materialpreTime;
}
