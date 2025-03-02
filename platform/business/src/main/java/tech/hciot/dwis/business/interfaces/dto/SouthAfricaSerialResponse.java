package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SouthAfricaSerialResponse {

  private String design;
  private String boreSize;
  private String orderNo;
  private String spindleNo;
  private String netWeight;
  private String grossWeight;
  private List<String> wheelSerial;
}
