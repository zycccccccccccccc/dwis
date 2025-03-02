package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class WMachineQuantity {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date opeDT;
  private String operator;
  private Integer machineNo;
  private String design;
  private Integer quantity;
  private Integer tag70;
  private Integer tag701;
  private Integer tag48;
  private Integer tag47;
  private Integer reworkQuantity;
}
