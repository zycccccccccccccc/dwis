package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ReworkShiftLeaderData {

  private String inspectorId;
  private Integer preAmount = 0;
  private Integer scrapCodeAmount = 0;
  private BigDecimal scrapCodePrecheckPercent;
  private List<String> detail;
}
