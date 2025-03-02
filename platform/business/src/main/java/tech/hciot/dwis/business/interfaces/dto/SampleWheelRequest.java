package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.SampleWheelRecord;

@Data
public class SampleWheelRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String checkCode;
  private String design;

  public SampleWheelRecord convert2Model() {
    SampleWheelRecord target = new SampleWheelRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
