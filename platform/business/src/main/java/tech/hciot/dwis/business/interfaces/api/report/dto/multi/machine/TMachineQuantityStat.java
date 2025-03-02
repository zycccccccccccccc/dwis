package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import java.util.List;
import lombok.Data;

@Data
public class TMachineQuantityStat {


  private String operator;
  private Integer machineNo;
  private Integer quantity;
  private Integer tag138;
  private Integer tag8;
  private Integer tag51;
  private Integer tag52;
  private Integer tag53;
  private Integer tag54;
  private Integer tag55;
  private Integer tag56;
  private Integer reworkQuantity;
  private List<TMachineQuantity> detail;
}
