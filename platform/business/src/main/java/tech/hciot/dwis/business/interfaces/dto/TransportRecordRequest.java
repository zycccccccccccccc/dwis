package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.TransportRecord;

@Data
public class TransportRecordRequest {

  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String inspectorId;
  @NotNull
  private Integer opeType;
  private String design;
  private String boreSize;
  private String balanceS;

  public TransportRecord convert2Model() {
    TransportRecord target = new TransportRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
