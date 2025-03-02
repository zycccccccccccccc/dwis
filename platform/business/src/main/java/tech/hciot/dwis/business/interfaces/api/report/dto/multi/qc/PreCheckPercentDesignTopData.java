package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class PreCheckPercentDesignTopData {

  private List<PreCheckPercentDesignTotalData> list;
  private Integer amount = 0;
  private Integer noReworkAmount = 0;
  private Integer noReworkScrapAmount = 0;
  private Integer er3456 = 0;
  private Integer er3 = 0;
  private Integer er4 = 0;
  private Integer er5 = 0;
  private Integer er6 = 0;
  private BigDecimal noReworkPercent;
  private BigDecimal noReworkScrapPercent;
  private BigDecimal er3456Percent;
  private BigDecimal er3Percent;
  private BigDecimal er4Percent;
  private BigDecimal er5Percent;
  private BigDecimal er6Percent;
  private Integer otherAmount = 0;
  private BigDecimal otherPercent;
}
