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
public class GraphiteScrap {

  private List<QAStat> dragStatList; // 下箱
  private List<QAStat> copeStatList; // 上箱
}
