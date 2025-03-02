package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.MecProperty;

@Data
public class AddPerformanceRequest {

  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String design;
  @NotNull
  private Date testDate;
  @NotEmpty
  private String mecSerial;
  @NotNull
  private Date reportDate;
  @NotNull
  private Integer retest;
  @NotNull
  private BigDecimal residualStress;
  @NotNull
  private Integer tensile;
  @NotNull
  private BigDecimal elongation;
  @NotNull
  private Integer hardnessLocation1;
  @NotNull
  private Integer hardnessLocation2;
  @NotNull
  private Integer hardnessLocation3;
  @NotNull
  private BigDecimal impactAvg;
  @NotNull
  private BigDecimal impactMin;
  @NotNull
  private BigDecimal impactLocation1;
  @NotNull
  private BigDecimal impactLocation2;
  @NotNull
  private BigDecimal impactLocation3;

  public MecProperty convert2Model() {
    MecProperty target = new MecProperty();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
