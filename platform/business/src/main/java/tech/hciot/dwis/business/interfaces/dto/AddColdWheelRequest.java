package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.ColdWheel;

@Data
public class AddColdWheelRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;

  public ColdWheel convert2Model() {
    ColdWheel target = new ColdWheel();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
