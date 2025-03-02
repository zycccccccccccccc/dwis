package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class BalanceSummaryData {

  private String xh;
  private String reworkCode;
  private String holdCode;
  private String scrapCode;
  private Integer e3;
  private Integer mc;
  private Integer amount;
}
