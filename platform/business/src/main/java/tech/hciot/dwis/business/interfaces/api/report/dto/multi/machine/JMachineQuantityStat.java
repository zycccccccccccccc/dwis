package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import java.util.List;
import lombok.Data;

@Data
public class JMachineQuantityStat {

  private String operator;
  private Integer machineNo;
  private Integer quantity;
  private Integer tag69;
  private Integer tag691;
  private Integer tag6;
  private Integer tag45;
  private Integer tag9;
  private Integer reworkQuantity;
  private List<JMachineQuantity> detail;
}
