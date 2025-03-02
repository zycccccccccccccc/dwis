package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SmallTapeData {

  private String wheelSerial;
  private String design;
  private Integer wheelW;
  private BigDecimal tapeSize;
  private String scrapCode;
}
