package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class InStockRequest {

  @NotEmpty
  private String startDate;
  @NotEmpty
  private String endDate;
  @NotEmpty
  private String acceptanceNo;
  @NotEmpty
  private String stockDate;
  private String balanceFlag;
  private String design;
  private Integer boreSize;
  private String tapeSize;
  private Integer wheelW;
}
