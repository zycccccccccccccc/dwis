package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.ReleaseRecord;

@Data
public class AddReleaseRecordRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String reworkCode;

  public ReleaseRecord convert2Model() {
    ReleaseRecord target = new ReleaseRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
