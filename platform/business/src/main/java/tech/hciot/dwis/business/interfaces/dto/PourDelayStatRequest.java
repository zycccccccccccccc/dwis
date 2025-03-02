package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PourDelayStatRequest {

  private Integer scrapNum;
  private String delayCode;
  private String memo;
}
