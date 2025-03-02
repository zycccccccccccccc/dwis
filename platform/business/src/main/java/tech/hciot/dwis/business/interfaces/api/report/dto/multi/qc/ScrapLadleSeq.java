package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ScrapLadleSeq {

  private String scrapCode;
  private String wheelSerial;
  private String design;
  private BigDecimal pourTemp;
}
