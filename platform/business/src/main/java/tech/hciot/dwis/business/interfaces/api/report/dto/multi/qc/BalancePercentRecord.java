package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class BalancePercentRecord {

  private String design;
  @JsonFormat(pattern = "yyyy/M/d", timezone = "GMT+8")
  private Date opeDT;
  private String balanceInspectorId;
  private Integer amount = 0;
  private Integer qualifiedAmount = 0;
  private BigDecimal qualifiedPercent;
}
