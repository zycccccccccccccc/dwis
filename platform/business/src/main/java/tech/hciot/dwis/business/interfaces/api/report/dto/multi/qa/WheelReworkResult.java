package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WheelReworkResult {

  private WheelRework total;
  private List<WheelRework> resultList;
}
