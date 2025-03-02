package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scrap44AS {

  private String wheelSerial;
  private String cutId;
  private String scrapDate;
  private String hiHeatInDate;
  private String hiHeatInTime;
  private String hiHeatInShift;
  private String hiHeatInId;
  private String hiHeatInOperator;
  private String heatLine;
}
