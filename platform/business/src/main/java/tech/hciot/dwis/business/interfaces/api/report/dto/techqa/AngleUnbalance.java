package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AngleUnbalance {

  private String wheelSerial; // 轮号
  private String av;
  private String line; // 产线号
  private String balanceV;
  private String balanceA;
  private String opeDT; // 操作日期
  private String holdCode; // 保留代码
}
