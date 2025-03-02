package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WheelInStoreRequest {

  @NotEmpty
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private String startDate;
  @NotEmpty
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private String endDate;
  @NotEmpty
  private String design;
  private List<String> abc1;
  private Integer wheelSerialAsc;
  private Integer wheelAsc;
}
