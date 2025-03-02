package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class FinalCheckDateData {

  private FinalCheckTotalData total;
  private List<FinalCheckDateRecord> date;
}
