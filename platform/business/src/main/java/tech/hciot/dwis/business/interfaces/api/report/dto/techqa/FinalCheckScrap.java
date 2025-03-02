package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalCheckScrap {

  private String wheelSerial; // 轮号
  private String opeDT; // 操作日期
  private String scrapCode; // 报废代码
  private String scrapDate; // 报废日期
  private Integer confirmedScrap; // 是否报废
}
