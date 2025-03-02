package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class MagneticData {

  private String wheelSerial;
  private String design;
  private String reworkCode;
  private String scrapCode;
  private Integer ts;
}
