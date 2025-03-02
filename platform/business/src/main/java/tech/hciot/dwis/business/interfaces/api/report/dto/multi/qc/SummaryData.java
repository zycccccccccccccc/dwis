package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class SummaryData {

  private PreCheckData preCheck;
  private List<PreCheckTimesData> preCheckTimes;
  private FinalCheckData finalCheck;
  private UltraData ultra;
  private BalanceData balance;
  private List<XrayData> xray;
  private List<CihenData> cihen;
  private List<MagneticData> magnetic;
  private TransportData transport;
}
