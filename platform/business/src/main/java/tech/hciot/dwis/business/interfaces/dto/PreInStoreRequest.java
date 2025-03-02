package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PreInStoreRequest {

  @NotEmpty
  private String startTime;
  @NotEmpty
  private String endTime;
  @NotEmpty
  private String inStoreDate;
  @NotEmpty
  private String design;
  @NotEmpty
  private String checkCode;
}
