package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.Data;

@Data
public class JMachine8 {

  private String opeDT;  // 日期
  private String operator;  // 员工
  private String jMachineCount; // 基面加工毛轮数
  private String j6Count; // 加6轮数
  private String j6JMachineCount; // (加6轮数/基面加工毛轮数)%
  private String sconf8s; // 确废88S轮数
  private String j6Sconf8s; // 加6确废88S轮数
  private String miss6Count; // 漏加6轮数
  private String miss6JMachineCount; // (漏加6轮数/基面加工毛轮数)%
}
