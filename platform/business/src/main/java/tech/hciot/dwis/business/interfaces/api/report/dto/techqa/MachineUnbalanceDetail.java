package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineUnbalanceDetail {

  private String wheelSerial; // 轮号
  private String machineNo; // 机床号
  private String operator; // 操作工
  private String s2;
  private String s1;
  private String holdCode; // 保留代码
  private String opeDT; // 操作日期
}
