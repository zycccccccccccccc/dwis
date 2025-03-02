package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class PreCheckSummaryData {

  private String design;
  private Integer times;
  private Integer amount;
}
