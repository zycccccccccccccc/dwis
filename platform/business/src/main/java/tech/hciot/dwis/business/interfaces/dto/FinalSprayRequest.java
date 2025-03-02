package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.FinalSprayRecord;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FinalSprayRequest {
  @NotNull
  private Integer preShiftId;
  @NotNull
  private Integer lineNo;
  @NotNull
  private String operatorId;
  @NotNull
  private Integer measureTimes;
  private Integer measureLocation;
  @NotNull
  private Integer thickness;
  @NotNull
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date measureTime;

  public FinalSprayRecord convert2Model() {
    FinalSprayRecord target = new FinalSprayRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
