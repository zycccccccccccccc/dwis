package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ReworkShiftRecord {

  private String inspectorId;
  private String reworkCode;
  private Integer scrapAmount;
  private BigDecimal scrapPercent;
}
