package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class ScrapShiftMachineLeaderData {

  private String inspectorId;
  private Integer scrapAmount;
  private String type;
  private List<ScrapShiftMachineRecord> detail;
}
