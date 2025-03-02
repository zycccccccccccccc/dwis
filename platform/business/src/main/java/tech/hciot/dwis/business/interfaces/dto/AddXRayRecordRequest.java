package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.InspectionRecord;

@Data
public class AddXRayRecordRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String xrayResult;
  private String scrapResult;

  public InspectionRecord convert2Model() {
    InspectionRecord target = new InspectionRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
