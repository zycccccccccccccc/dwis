package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineStatResponse {

  private Integer calibraMachineCount; // 标准轮加工数量
  private Integer totalMachineCount; // 总加工数量
  private Integer leftMachineCount; // 左位加工数量
  private Integer rightMachineCount; // 右位加工数量
}
