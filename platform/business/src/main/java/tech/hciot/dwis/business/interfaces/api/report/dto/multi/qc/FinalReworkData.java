package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class FinalReworkData {

  private FinalReworkMachineData jMachine;
  private FinalReworkMachineData tMachine;
  private FinalReworkMachineData kMachine;
}
