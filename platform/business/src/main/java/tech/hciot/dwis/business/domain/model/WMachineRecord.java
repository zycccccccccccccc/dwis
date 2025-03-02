package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicInsert
@EqualsAndHashCode(callSuper = true)
public class WMachineRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotNull
  private Integer machineNo;
  private String operator;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  @NotNull
  private Integer wS1;
  @NotNull
  private Integer wS2;
  @NotNull
  private BigDecimal hubExradius;
  @NotNull
  private BigDecimal plateThickness;
  private BigDecimal rimThickness;
  @NotNull
  private Integer machinedStep;
  private String reworkCode;
  private Integer isMeasureCheck;
  private Integer isInspecCheck;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
  private String memo;

  @Transient
  private Integer rework;
}
