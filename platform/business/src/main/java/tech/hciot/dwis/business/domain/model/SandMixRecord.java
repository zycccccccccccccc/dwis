package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandMixRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String operatorId;
  private String inspectorId;
  private Integer shift;
  private Integer lineNo;
  private BigDecimal sandTemp;
  private BigDecimal waterGlassTemp;
  private BigDecimal quartzSand;
  private BigDecimal waterGlass;
  private BigDecimal waterGlassPercent;
  private BigDecimal coalAsh;
  private BigDecimal coalAshPercent;
  private Integer sandBreathability;
  private Integer sandBreathabilityLimits;
  private Integer sandTempLimits;
  private Integer waterGlassTempLimits;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date weighTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  @Transient
  private List<String> highlight;
}
