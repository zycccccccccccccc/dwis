package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreScrap {

  private String opeDate;
  private String design;
  private String pre;
  private String scrap;
  private String pct;
}
