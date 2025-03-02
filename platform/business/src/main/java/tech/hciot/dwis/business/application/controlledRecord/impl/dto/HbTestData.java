package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class HbTestData {

  private Date testDate;
  private String shiftNo;
  private String testBlockNo;
  private Integer standValue;
  private Integer testResult;
  private BigDecimal indentaDia;
  private BigDecimal MIndentaDia;
  private BigDecimal devIndentaDia;
  private String result;
  private String operator;
}
