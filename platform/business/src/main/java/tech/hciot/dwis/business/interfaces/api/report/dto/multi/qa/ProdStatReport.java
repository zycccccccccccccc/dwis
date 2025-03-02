package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdStatReport {

  private List<ProdStatByDay> dataList; // 每日数据部分
  private ProdStatTotal totalStat; // 统计部分
}
