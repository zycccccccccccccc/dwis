package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineScrap {

  private String wheelSerial; // 轮号
  private String opeDT; // 加工日期
  private String s2; // S2
  private String machineNo; // 机床号
  private String operator; // 操作工号
  private String scrapCode; // 报废代码
  private Integer confirmedScrap; // 是否报废
  private Integer finished; // 是否成品
}
