package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import java.util.List;
import lombok.Data;

@Data
public class QMachineQuantityStat {

  private String operator;
  private Integer machineNo;
  private Integer quantity;
  private Integer q1;
  private Integer q2;
  private List<QMachineQuantity> detail;
}
