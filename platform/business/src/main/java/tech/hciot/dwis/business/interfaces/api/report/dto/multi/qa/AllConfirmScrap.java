package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllConfirmScrap {

  private String scrapCode;
  private String scrap;
  private String scrapPct;
  private String confirmedScrap;
}
