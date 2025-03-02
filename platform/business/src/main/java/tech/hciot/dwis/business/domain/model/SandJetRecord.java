package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandJetRecord {

  //完成射砂，等待浇注
  public static final Integer STATUS_JETTED = 1;
  //完成浇注，等待开箱
  public static final Integer STATUS_POURED = 2;
  //完成开箱，等待射砂
  public static final Integer STATUS_UNBOX = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer preShiftId;
  private String operatorId;
  private String graphite;
  private String wheelSerial;
  private String metalMold;
  private BigDecimal metalTemp;
  private BigDecimal co2Consumption;
  private BigDecimal graphiteTemp;
  private Integer co2HardeningTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date jetTime;
  private Integer status;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date moldAge;
  private Integer lineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;
}
