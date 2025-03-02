package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class UltraSummaryData {

  private String reworkCode;
  private String holdCode;
  private String scrapCode;
  private String xh;
  private String xray_req;
  private Integer amount;
  private Integer total1;
  private Integer total2;
}
