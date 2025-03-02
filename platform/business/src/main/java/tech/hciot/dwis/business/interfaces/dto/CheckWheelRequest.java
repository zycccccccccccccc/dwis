package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CheckWheelRequest {

  @NotEmpty
  private String startDate;
  @NotEmpty
  private String endDate;

  private List<String> abc;
  private Integer finish = 1;
  private Integer abcNull = 0;
  private Integer wheelNull = 0;
}
