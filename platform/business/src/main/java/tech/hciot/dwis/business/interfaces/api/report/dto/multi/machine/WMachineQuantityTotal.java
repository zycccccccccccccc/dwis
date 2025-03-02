package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.Data;

@Data
public class WMachineQuantityTotal {

  private Integer quantity = 0;
  private Integer tag70 = 0;
  private Integer tag701 = 0;
  private Integer tag48 = 0;
  private Integer tag47 = 0;
  private Integer reworkQuantity = 0;
}
