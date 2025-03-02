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
public class QAStat {

  private String statKey;
  private String castDate; // 浇注日期
  private String productDate; //生产日期

  private String majorTitle; // 一层标题
  private String minorTitle; // 二层标题
  private String subTitle; // 三层标题
  
  private String castTotal; // 总数
  private String preInsp; // 预检数
  private String preCast; // 预检/浇注
  private String scrapSum; // 废码数
  private String scrapPre; // 废码/预检
  private String sconfSum; // 确废数
  private String sconfPre; // 确废/预检
  private String toDock; // 成品数
  private String toStock; //入库数
  private String toDockPre; // 成品/预检
  private String toDockDockAndSconf; // 成品/(成品+确废)
  private String scrapDockAndSconf; // 废码/(成品+确废)
  private String sconfDockAndSconf; // 确废/(成品+确废)
  private String scrapDockAndScrap; // 废码/(成品+废码)
  private String toDockDockAndScrap; // 成品/(成品+废码)
  private String sconfStockAndSconf; //确废/（入库+确废）

  private List<QAData> data;

  // 是否有数据，如果总数为空或为0，则返回false，否则返回true
  public boolean hasData() {
    if (castTotal == null || castTotal.equals("0")) {
      return false;
    }
    return true;
  }
}
