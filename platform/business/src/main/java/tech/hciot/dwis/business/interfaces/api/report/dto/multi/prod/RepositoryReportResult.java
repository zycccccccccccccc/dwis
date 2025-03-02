package tech.hciot.dwis.business.interfaces.api.report.dto.multi.prod;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryReportResult {

  private JSONObject finish;
  private JSONObject machining;
  private Integer total;
}
