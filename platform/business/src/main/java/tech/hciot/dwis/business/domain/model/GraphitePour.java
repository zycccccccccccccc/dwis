package tech.hciot.dwis.business.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphitePour {

  private String graphite;
  private Integer cd;
  private Integer cnt; // 浇注数量
  private Integer status;
}
