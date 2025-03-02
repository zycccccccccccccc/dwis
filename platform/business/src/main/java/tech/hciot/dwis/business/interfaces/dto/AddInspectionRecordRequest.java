package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.InspectionRecord;

@Data
public class AddInspectionRecordRequest {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  private String leaderId;
  private String testCode;
  private String holdCode;
  private String heatCode;
  private String scrapCode;
  private String reworkCode;
  private Integer brinReq;
  private Integer specialUltra;
  private Integer xrayReq;
  private Integer specialMt;

  public InspectionRecord convert2Model() {
    InspectionRecord target = new InspectionRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
