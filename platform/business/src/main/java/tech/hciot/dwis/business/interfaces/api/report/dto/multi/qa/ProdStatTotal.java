package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdStatTotal {

  private String total; // 在制总数
  private String machineTotal; // 在制加工
  private String xray; // XRAY
  private String finishedTotal;  // 在制成品
  private String scrap; // 有废品代码
  private String unheat; // 没热处理
  private String heated; // 热处理后
  private String machiningArht; // 进加工的ARHT
  private String unmachineArht; // 没进加工的ARHT
}
