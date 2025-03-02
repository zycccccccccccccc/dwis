package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PourTimeAndScrap {

  private String castDate; // 日期
  private Integer tapSeq; // 出钢号
  private String bb1;
}
