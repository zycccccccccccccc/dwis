package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapWheelDetail {

  private String wheelSerial;
  private String design;
  private String scrapCode;
  private Integer xrayReq;
  private String reworkCode;
  private String testCode;
  private String mecSerial;
  private String heatKey;
}
