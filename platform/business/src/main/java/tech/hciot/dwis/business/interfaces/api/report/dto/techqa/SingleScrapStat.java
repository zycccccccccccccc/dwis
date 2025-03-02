package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleScrapStat {

  private List<SingleScrap> total;
  private List<SingleScrap> xh;
  private List<SingleScrap> model;
}
