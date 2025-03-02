package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class PreCheckPercentData {

  private PreCheckPercentDateTopData date;
  private PreCheckPercentDesignTopData design;
  private PreCheckPercentStaffTopData staff;
  private PreCheckPercentOutTopData out;
}
