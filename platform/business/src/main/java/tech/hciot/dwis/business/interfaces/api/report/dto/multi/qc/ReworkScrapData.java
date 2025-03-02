package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class ReworkScrapData {

  private List<PreCheckReworkScrapData> preCheck;
  private List<PreCheckReworkScrapData> finalCheck;
  private List<PreCheckReworkScrapData> ultra;
  private List<PreCheckReworkScrapData> magnetic;
  private List<BalanceReworkScrapData> balance;
}
