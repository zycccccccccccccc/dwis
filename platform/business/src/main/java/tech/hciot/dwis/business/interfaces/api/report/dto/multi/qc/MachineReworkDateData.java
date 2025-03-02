package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class MachineReworkDateData {

  private String opeDT;
  private Integer reworkAmount = 0;
  private Integer finishAmount = 0;
  private Integer scrapAmount = 0;
  List<MachineReworkTotalDetail> detail;
}
