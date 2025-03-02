package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ReworkPercentData {

  private Integer amount = 0;
  private Integer passedAmount = 0;
  private BigDecimal passedPercent;
  private List<ReworkPercentDateData> list;
}
