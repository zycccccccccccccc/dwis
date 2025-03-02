package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import java.util.List;
import lombok.Data;

@Data
public class KMachineQuantityStat {

  private String operator;
  private Integer machineNo;
  private Integer quantity;
  private Integer tag40;
  private Integer tag44;
  private Integer tag43;
  private Integer tag46;
  private Integer tag30;
  private Integer reworkQuantity;
  private List<KMachineQuantity> detail;
}
