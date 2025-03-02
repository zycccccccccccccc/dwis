package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class FinalCheckPercentRecord {

  private String copeInspectorID;
  private String design;
  @JsonFormat(pattern = "yyyy/M/d", timezone = "GMT+8")
  private Date opeDT;
  private Integer amount = 0;
  private Integer noReworkAmount = 0;
  private Integer noScrapAmount = 0;
  private Integer noReworkScrapAmount = 0;
  private Integer otherAmount = 0;
  private BigDecimal noReworkPercent;
  private BigDecimal noReworkScrapPercent;
  private BigDecimal noScrapPercent;
  private BigDecimal otherPercent;
}
