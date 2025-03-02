package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangeShippedNoRequest {

  @NotEmpty
  private String hgz;
  @NotEmpty
  private String shippedId;
  @NotEmpty
  private String trainNo;
  @NotEmpty
  private String shippedDate;
  @NotEmpty
  private String customerId;
}
