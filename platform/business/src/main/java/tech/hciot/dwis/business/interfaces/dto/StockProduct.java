package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockProduct {

  private Integer boreSize;
  private BigDecimal tapeSize;
  private Integer wheelW;
  private String e3;
  private List<String> productCodeList;
  private Integer sum;
}
