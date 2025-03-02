package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class BalanceData {

  private List<BalanceSummaryData> balance;
  private Integer line1Total;
  private Integer line2Total;
  private Integer balanceTotal;
//  private List<TapeSummaryData> tape;
  private JSONObject tape;
/*
  private Integer e3Total;
  private Integer tapeTotal;
  private Integer smallTapeTotal;
*/
}
