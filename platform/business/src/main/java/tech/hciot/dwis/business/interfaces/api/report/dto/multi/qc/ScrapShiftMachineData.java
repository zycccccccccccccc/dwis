package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ScrapShiftMachineData {

  private Integer amount;
  private Integer scrapAmount;
  private BigDecimal scrapPercent;
  private List<ScrapShiftQcTopData> top;
  private List<ScrapShiftMachineLeaderData> jMachine;
  private List<ScrapShiftMachineLeaderData> tMachine;
  private List<ScrapShiftMachineLeaderData> kMachine;
  private List<ScrapShiftMachineLeaderData> wMachine;
}
