package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class DipElectrodeInfo {

  private Integer times;
  private Date timeBegin;
  private Date timeEnd;
}
