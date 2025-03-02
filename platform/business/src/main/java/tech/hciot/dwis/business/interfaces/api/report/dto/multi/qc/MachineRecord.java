package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class MachineRecord {

  private String wheelSerial;
  private Integer machineNo;
  private String operator;
  private String reworkCode;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date finalFirstTime;
}
