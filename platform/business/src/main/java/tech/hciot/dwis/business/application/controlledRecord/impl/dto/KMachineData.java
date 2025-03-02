package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class KMachineData {

  private String location;
  private String wheelSerial;
  private BigDecimal concentricity;
  private BigDecimal boreDia;
  private String memo;
}
