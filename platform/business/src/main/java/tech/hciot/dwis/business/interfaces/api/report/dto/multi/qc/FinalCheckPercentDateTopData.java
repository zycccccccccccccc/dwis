package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class FinalCheckPercentDateTopData {

  private List<FinalCheckPercentDateTotalData> list;
  private Integer amount = 0;
  private Integer noReworkAmount = 0;
  private Integer noScrapAmount = 0;
  private Integer noReworkScrapAmount = 0;
  private Integer otherAmount = 0;
  private BigDecimal noReworkPercent;
  private BigDecimal noReworkScrapPercent;
  private BigDecimal noScrapPercent;
  private BigDecimal otherPercent;
}
