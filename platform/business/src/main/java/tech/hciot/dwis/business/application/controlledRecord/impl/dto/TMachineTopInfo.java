package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class TMachineTopInfo {

  private String operator;
  private Integer machineNo;
  private Date opeDT;
  private String inspectorId;
  private Integer isMeasureCheck;
}
