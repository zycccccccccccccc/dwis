package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WheelStockData {

  private String index;
  private String balanceFlag;
  private String design;
  private String boreSize;
  private String tapeSize;
  private String wheelW;
  private Integer amount;
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date date;
  private List<String> wheels;
  @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
  private Date reportDate;
}
