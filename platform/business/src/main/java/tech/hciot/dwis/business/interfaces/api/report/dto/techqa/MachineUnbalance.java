package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineUnbalance {

  private String wheelSerial; // 轮号
  private List<MachineUnbalanceDetail> detail; // 详情
}
