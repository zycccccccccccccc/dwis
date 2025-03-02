package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class ScrapShiftMachineRecord {

  private String inspectorId;
  private String operator;
  private String scrapCode;
  private Integer scrapAmount;
  private String type;
}
