package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class XRayCheckResponse {

  private Integer ladleId;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date castDate;
  private Integer furnaceNo;
  private Integer heatSeq;
  private Integer tapSeq;
  private Integer ladleSeq;
  private BigDecimal ladleTemp;
  private String design;
  private Integer pourTime;
  private BigDecimal c;
}
