package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class MachineReworkTotalDetail {

  private String opeDT;
  private String reworkCode;
  private Integer reworkAmount = 0;
  private Integer finishAmount = 0;
  private Integer scrapAmount = 0;
  private BigDecimal reworkPercent;
  private BigDecimal reworkFinishPercent;
  private BigDecimal reworkScrapPercent;
}
