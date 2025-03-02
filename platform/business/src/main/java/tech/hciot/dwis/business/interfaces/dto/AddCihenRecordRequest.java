package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.CihenRecord;

@Data
public class AddCihenRecordRequest {

  @NotEmpty
  private String dragInspectorId;
  @NotEmpty
  private String copeInspectorId;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  private Integer grindTime;
  private Integer copeCihenSum;
  private Integer copeSandholes;
  private Integer dragCihenSum;
  private Integer dragSandholes;
  private String cihenCode;
  private String foreCihenCode;
  private String scrapCode;

  public CihenRecord convert2Model() {
    CihenRecord target = new CihenRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
