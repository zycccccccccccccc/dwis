package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class FinalCheckReworkDateDetailRecord {

  private Date opeDT;
  private String reworkCode;
  private Integer amount = 0;
  private BigDecimal finalCheckPercent;
  private String xh;
}
