package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ReworkShiftCheckData {

  private Integer preAmount;
  private Integer scrapCodeAmount;
  private BigDecimal scrapCodePrecheckPercent;
  private List<ReworkShiftLeaderData> data;
}
