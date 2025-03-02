package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class FinalCheckReworkRecord {

  private String reworkCode;
  private Integer amount = 0;
  private BigDecimal finalCheckPercent;
}
