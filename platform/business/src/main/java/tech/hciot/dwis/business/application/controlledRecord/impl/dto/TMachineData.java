package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TMachineData {

  private String wheelSerial;
  private BigDecimal rimWidth;
  private BigDecimal hubLength;
  private Integer flangeTreadProfile;
  private BigDecimal rollingCircleDia;
  private BigDecimal rimdev1;
  private BigDecimal rimdev2;
  private BigDecimal rimdev3;
  private String design;
}
