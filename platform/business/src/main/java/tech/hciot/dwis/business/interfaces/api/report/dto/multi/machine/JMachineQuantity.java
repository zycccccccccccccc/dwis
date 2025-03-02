package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class JMachineQuantity {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date opeDT;
  private String operator;
  private Integer machineNo;
  private String design;
  private Integer quantity;
  private Integer tag69;
  private Integer tag691;
  private Integer tag6;
  private Integer tag45;
  private Integer tag9;
  private Integer reworkQuantity;
}
