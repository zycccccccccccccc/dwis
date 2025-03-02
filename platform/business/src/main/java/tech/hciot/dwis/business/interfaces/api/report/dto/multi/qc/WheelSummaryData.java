package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class WheelSummaryData {

  private Integer internal;
  private String design;
  private Integer amount;
  private String reworkCode;
  private String scrapCode;
}
