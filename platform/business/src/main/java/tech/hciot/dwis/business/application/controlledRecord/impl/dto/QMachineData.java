package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class QMachineData {

  private String wheelSerial;
  private Integer originalGm;
  private BigDecimal chuck1;
  private BigDecimal pad1;
  private BigDecimal deviation;
  private String memo;
}
