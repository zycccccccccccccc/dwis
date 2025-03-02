package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TransferDataRequest {

  @NotEmpty
  private String shippedId;
  @NotEmpty
  private List<String> abc;
}
