package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class FinalCheckReworkData {
  private FinalCheckDateData check;
  private FinalCheckDateData pour;
}
