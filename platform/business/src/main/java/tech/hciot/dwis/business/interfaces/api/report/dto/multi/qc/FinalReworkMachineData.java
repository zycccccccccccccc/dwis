package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class FinalReworkMachineData {

  private FinalReworkStatData noMachine;
  private FinalReworkStatData hasMachine;
}
