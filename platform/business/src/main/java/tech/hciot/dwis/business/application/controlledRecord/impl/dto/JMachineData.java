package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class JMachineData {

  private String wheelSerial;
  private BigDecimal f;
  private BigDecimal d2Dia;
  private BigDecimal d2Cir;
  private String memo;
}
