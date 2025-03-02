package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class TMachineQuantity {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date opeDT;
  private String operator;
  private Integer machineNo;
  private String design;
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
}
