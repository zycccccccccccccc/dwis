package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipCheckCodeResponse {

  private Integer amount;
  private String design;
  private Integer boreSize;
  private BigDecimal tapSize;
  private Integer wheelW;
  private String balanceS;
  private String hgz;
  private String mecSerial;
  private String testCode;
}
