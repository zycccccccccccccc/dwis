package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class KMachineQuantity {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date opeDT;
  private String operator;
  private Integer machineNo;
  private String location;
  private String design;
  private Integer quantity;
  private Integer tag40;
  private Integer tag44;
  private Integer tag43;
  private Integer tag46;
  private Integer tag30;
  private Integer reworkQuantity;
}
