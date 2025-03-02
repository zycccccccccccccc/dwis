package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class WheelRecordResponse {

  private String wheelSerial;
  private String design;
  private String heatCode;
  private String holdCode;
  private String reworkCode;
  private String testCode;
  private String scrapCode;
  private Integer xrayReq;
  private Integer specialUltra;
  private Integer brinReq;
  private Integer brinnelReading;
  private Integer specialMt;
  private String cihenCode;
  private Integer boreSize;
  private BigDecimal tapeSize;
  private Integer wheelW;
  private String balanceS;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date stockDate;
  private String shippedNo;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date scrapDate;
  private String checkCode;
  private String mecSerial;
}
