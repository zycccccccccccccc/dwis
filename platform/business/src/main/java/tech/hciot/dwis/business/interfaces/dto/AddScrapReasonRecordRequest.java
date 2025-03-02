package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.ScrapReasonRecord;

@Data
public class AddScrapReasonRecordRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  private String design;
  private String scrapCode;
  @NotEmpty
  private String scrapReasonCode;


  public ScrapReasonRecord convert2Model() {
    ScrapReasonRecord target = new ScrapReasonRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
