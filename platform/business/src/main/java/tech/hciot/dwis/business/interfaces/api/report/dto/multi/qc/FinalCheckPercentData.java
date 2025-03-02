package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class FinalCheckPercentData {

  private FinalCheckPercentDateTopData date;
  private FinalCheckPercentDesignTopData design;
  private FinalCheckPercentStaffTopData staff;
}
