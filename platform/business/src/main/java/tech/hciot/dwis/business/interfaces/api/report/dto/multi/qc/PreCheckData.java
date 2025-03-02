package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class PreCheckData {

  private List<WheelSummaryData> wheel;
  private Integer internalAmount;
  private Integer otherAmount;
  private Integer totalAmount;
  private List<PreCheckSummaryData> preCheck;
  private List<PreCheckScrapData> preCheckScrap;
  private Integer preCheckScrapTotal;
}
