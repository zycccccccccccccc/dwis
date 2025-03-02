package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SPCChartData {

  private Integer meaValue;
  private Integer minValue;
  private Integer maxValue;
  @JsonFormat(pattern = "yy/M/d HH:mm", timezone = "GMT+8")
  private Date moldDate;
}
