package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoldPreShiftRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date moldDate;
  private Integer cd;
  private Integer shift;
  private String inspectorId;
  private BigDecimal siteTemp;
  private BigDecimal siteHumidity;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date jetBarrelCleanTime1;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date jetBarrelCleanTime2;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;

  @Transient
  private String operatorId;
  @Transient
  private String metalMoldOdd;
  @Transient
  private BigDecimal metalTempOdd;
  @Transient
  private Integer co2HardeningTimeOdd;
  @Transient
  private String metalMoldEven;
  @Transient
  private BigDecimal metalTempEven;
  @Transient
  private Integer co2HardeningTimeEven;
}
