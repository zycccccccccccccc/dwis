package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class XRayCheckData {

  private String ladleRecordKey;
  @NotEmpty
  private String wheelSerial;
  private String design;
  private Integer confirmedScrap;
  private Integer finished;
  @NotEmpty
  private String inspectorId;
  @NotNull
  private Integer xrayReq;
}
