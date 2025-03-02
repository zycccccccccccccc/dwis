package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.PreSprayRecord;
import tech.hciot.dwis.business.domain.model.SandMixRecord;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PreSprayRequest {
  @NotNull
  private Integer preShiftId;
  @NotNull
  private Integer lineNo;
  @NotNull
  private String operatorId;
  @NotNull
  private Integer measureTimes;
  @NotNull
  private Integer thickness;
  @NotNull
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date measureTime;

  public PreSprayRecord convert2Model() {
    PreSprayRecord target = new PreSprayRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
