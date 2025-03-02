package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockData {

  private String index;
  private String spec;
  @Default
  private String drawingNo = "";
  private Integer amount;
  @JsonFormat(pattern = "yyyy年MM月dd日", timezone = "GMT+8")
  private Date date;
  private List<StockProduct> products;
}
