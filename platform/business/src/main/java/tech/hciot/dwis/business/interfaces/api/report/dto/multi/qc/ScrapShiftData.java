package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class ScrapShiftData {

  private ScrapShiftQcData qc;
  private ScrapShiftModelData model;
  private ScrapShiftQcData furnace;
  private ScrapShiftMachineData machine;
}
