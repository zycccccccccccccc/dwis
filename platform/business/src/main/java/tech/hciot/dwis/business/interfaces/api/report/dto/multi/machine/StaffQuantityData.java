package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.Data;

@Data
public class StaffQuantityData {

  private QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jMachine;
  private QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tMachine;
  private QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kMachine;
  private QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wMachine;
  private QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qMachine;
}
