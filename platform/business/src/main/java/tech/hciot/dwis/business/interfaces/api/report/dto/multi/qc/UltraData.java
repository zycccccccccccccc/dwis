package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class UltraData {

  private List<UltraSummaryData> ultra;
  private Integer line1Total;
  private Integer line2Total;
  private Integer ultraTotal;

}
