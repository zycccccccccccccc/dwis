package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleScrap {

  private String majorTitle;
  private String minorTitle;
  private String castTotal;
  private String scrapSum;
  private String preInsp;
  private String scrapPre;
}
