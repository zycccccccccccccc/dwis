package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.SandMixRecord;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SandMixRequest {

  @NotNull
  private Integer shift;
  @NotNull
  private Integer lineNo;
  @NotNull
  private String operatorId;
  @NotNull
  private String inspectorId;
  @NotNull
  private BigDecimal sandTemp;
  @NotNull
  private BigDecimal waterGlassTemp;
  @NotNull
  private BigDecimal quartzSand;
  @NotNull
  private BigDecimal waterGlass;
  @NotNull
  private BigDecimal coalAsh;

  private Integer sandBreathability;
  @NotNull
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date weighTime;

  public SandMixRecord convert2Model() {
    SandMixRecord target = new SandMixRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
