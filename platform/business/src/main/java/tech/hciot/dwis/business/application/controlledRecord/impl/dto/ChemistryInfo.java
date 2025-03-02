package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ChemistryInfo {

  private String sampleNo;
  private BigDecimal c;
  private BigDecimal si;
  private BigDecimal mn;
  private BigDecimal p;
  private BigDecimal s;
  private BigDecimal cr;
  private BigDecimal crNiMo;
}
