package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class ReworkShiftData {

  private ReworkShiftCheckData precheck;
  private ReworkShiftCheckData finalcheck;
}
