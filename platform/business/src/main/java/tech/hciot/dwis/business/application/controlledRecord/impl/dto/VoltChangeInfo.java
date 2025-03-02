package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class VoltChangeInfo {

  private Integer times;
  private Integer volt;
  private Date timeBegin;
  private Date timeEnd;
}
