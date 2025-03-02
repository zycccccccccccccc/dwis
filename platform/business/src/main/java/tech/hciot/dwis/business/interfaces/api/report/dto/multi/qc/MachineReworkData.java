package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class MachineReworkData {

  private Integer amount;
  private BigDecimal reworkPercent;
  private BigDecimal reworkFinishPercent;
  private BigDecimal reworkScrapPercent;
  private MachineReworkTotalData total;
  private List<MachineReworkDateData> data;
}
