package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalSerialResponse {

  @Default
  private String manufacturer = "DCACC";
  private String shelfNumber;
  private String design;
  private String shippedNo;
  private Integer maxBoreSize;
  private Integer minBoreSize;
  private String dataMatrix;
}
