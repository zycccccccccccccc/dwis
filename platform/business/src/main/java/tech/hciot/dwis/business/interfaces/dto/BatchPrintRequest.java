package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BatchPrintRequest {

  @NotEmpty
  private List<String> wheelSerial;
  private Boolean designAsc;
  private Boolean finishedAsc;
}
