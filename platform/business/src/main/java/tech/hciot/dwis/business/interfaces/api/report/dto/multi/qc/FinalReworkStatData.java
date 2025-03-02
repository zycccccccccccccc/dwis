package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class FinalReworkStatData {

  private Integer amount;
  private List<MachineRecord> detail;

}
