package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class DesignSummaryData {

  private String xh;
  private String design;
  private Integer amount;
  private String reworkCode;
  private String scrapCode;
}
