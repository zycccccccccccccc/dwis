package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QAData {

  private String statKey;
  private String scrapCode; // 报废代码
  private String cnt; // 总数
  private String cntPre; // 总数/预检数
  private String sconfSum; // 确废数
  private String sconfDockAndSconf; // 确废/(成品+确废)

  private String detail; // 废品代码 - 数量 - 数量/预检数量
  private String detail2; // 废品代码 - 确废数量 - 确废数量/(确废数量+成品数量)
  private String detail3; // 废品代码 - 数量 - 数量/(废码数量+成品数量)
}
