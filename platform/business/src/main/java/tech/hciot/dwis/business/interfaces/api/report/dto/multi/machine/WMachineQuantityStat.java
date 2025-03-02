package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import java.util.List;
import lombok.Data;

@Data
public class WMachineQuantityStat {

  private String operator;
  private Integer machineNo;
  private Integer quantity;
  private Integer tag70;
  private Integer tag701;
  private Integer tag48;
  private Integer tag47;
  private Integer reworkQuantity;
  private List<WMachineQuantity> detail;
}
