package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.CihenRecordPre;

@Data
public class AddCihenRecordPreRequest {


  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  private String scrapCode;
  private Integer copeSandholes;
  private Integer dragSandholes;


  public CihenRecordPre convert2Model() {
    CihenRecordPre target = new CihenRecordPre();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
