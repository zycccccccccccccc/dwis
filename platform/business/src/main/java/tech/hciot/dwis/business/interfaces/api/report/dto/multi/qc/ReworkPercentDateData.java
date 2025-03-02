package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ReworkPercentDateData {

  @JsonFormat(pattern = "yyyy/M/d", timezone = "GMT+8")
  private Date opeDT;
  private Integer amount = 0;
  private Integer passedAmount = 0;
  private BigDecimal passedPercent;
  private List<ReworkPercentCodeData> data;
}
