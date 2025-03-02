package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdStatByDay {

  private String castDate; // 日期
  @Default
  private String whlCast = "0"; // 浇注车轮
  @Default
  private String tlInPit = "0"; // 进缓冷桶
  @Default
  private String tlHt = "0"; // 热处理车轮
  @Default
  private String preInsp = "0"; // 预检车轮
  @Default
  private String finalInsp = "0"; // 终检车轮
  @Default
  private String machine = "0"; // 加工
  @Default
  private String toDock = "0"; // 成品入库
  @Default
  private String finScrap = "0"; // 确认报废
  @Default
  private String shipped = "0"; // 发运
}
