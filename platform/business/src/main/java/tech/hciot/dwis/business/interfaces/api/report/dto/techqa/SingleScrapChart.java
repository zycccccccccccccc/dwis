package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleScrapChart {

  private List<Map<String, String>> totalChart;
  private List<Map<String, String>> xhChart;
  private List<Map<String, String>> modelChart;
}
