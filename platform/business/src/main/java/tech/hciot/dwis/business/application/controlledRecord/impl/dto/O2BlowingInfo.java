package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class O2BlowingInfo {

  private Integer times;
  private String pressure;
  private Date timeBegin;
  private Date timeEnd;
}
