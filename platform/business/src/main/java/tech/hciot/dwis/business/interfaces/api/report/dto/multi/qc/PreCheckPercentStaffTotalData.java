package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class PreCheckPercentStaffTotalData {

  private String copeInspectorID;
  private Integer amount;
  private Integer noReworkAmount;
  private Integer noReworkScrapAmount;
  private Integer er3456;
  private Integer er3;
  private Integer er4;
  private Integer er5;
  private Integer er6;
  private BigDecimal noReworkPercent;
  private BigDecimal noReworkScrapPercent;
  private BigDecimal er3456Percent;
  private BigDecimal er3Percent;
  private BigDecimal er4Percent;
  private BigDecimal er5Percent;
  private BigDecimal er6Percent;
  private Integer otherAmount;
  private BigDecimal otherPercent;
  private List<PreCheckPercentRecord> detail;
}
