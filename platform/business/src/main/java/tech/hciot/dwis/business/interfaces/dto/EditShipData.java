package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EditShipData {

  @NotEmpty
  private Integer serialNo;
  @NotEmpty
  private String hgz;
  private String hgzNew;
  private String wheelSerial;
}
