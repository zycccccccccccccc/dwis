package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class PreCheckScrapData {

  private String design;
  private String scrapCode;
  private Integer amount;
}
