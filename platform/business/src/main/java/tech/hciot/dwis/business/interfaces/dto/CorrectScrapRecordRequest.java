package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.ScrapRecord;

@Data
public class CorrectScrapRecordRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String design;
  private String scrapCode;


  public ScrapRecord convert2Model() {
    ScrapRecord target = new ScrapRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
