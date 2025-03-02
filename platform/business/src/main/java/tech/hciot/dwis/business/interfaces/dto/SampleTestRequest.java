package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SampleTestRequest {


  @NotNull
  private Integer id;
  @NotNull
  private Integer isInspecCheck;
}
