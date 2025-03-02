package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipDataResponse {

  private Integer serialNo;
  private String hgz;
  private String wheelSerial;
  private String design;
  @JsonFormat(pattern = "yy-M-d", timezone = "GMT+8")
  private Date stockDate;
  private Integer boreSize;
  private BigDecimal tapeSize;
  private Integer wheelW;
  private String balanceS;
  private String shippedNo;
  private String shelfNo;
  private String shelfNumber;
  private String flag;
  private Integer finished;
  private String checkCode;
  @JsonFormat(pattern = "yy-M-d HH:mm", timezone = "GMT+8")
  private Date createTime;
  private String mecSerial;
  private String testCode;
}
