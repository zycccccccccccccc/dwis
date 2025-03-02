package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class FinalCheckData {

  private List<DesignSummaryData> design;
  private Integer cj33Amount;
  private Integer sa34Amount;
  private Integer otherAmount;
  private Integer totalAmount;
  private Integer line1Amount;
  private Integer line2Amount;
  private Integer totalLineAmount;
  private Integer h1Amount;
  private Integer h6Amount;
  private Integer tirAmount;
  private Integer totalReworkAmount;
}
