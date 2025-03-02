package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class TapeSummaryData {

  private String design;
  private Integer boreSize;
  private Integer e3;
  private Integer amount;
  private Integer smallTape;
}
