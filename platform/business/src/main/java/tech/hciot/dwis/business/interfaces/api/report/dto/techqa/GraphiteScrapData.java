package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphiteScrapData {
  private String castTotal; // 总数
  private String preInsp; // 预检数
  private String scrapSum; // 废码数
}
