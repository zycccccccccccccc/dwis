package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.PitRecords;

@Data
public class PitRecordsRequest {

  @NotNull
  private Integer pitNo;
  @NotEmpty
  private String craneInId;
  @NotEmpty
  private String openId;


  public PitRecords convert2Model() {
    PitRecords target = new PitRecords();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
