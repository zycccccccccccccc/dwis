package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.Data;

@Data
public class MachineStaff {

  private String teamLeaderId;  // 工长工号
  private String operator;  // 员工工号
  private String reworkCode; // 返工代码
  private String reworkCount; // 返工数量
  private String machineCount; // 加工数量
  private String reworkMachine; // 返/加%
}
