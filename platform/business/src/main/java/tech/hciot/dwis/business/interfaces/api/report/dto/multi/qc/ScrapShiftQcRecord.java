package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ScrapShiftQcRecord {

  private String inspectorId;
  private String scrapCode;
  private Integer amount;
  private Integer scrapAmount;
  private BigDecimal scrapPercent;
}
