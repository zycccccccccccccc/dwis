package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class BalancePercentData {

  List<BalancePercentTotalData> data;
  private Integer amount = 0;
  private Integer qualifiedAmount = 0;
  private BigDecimal qualifiedPercent;
}
