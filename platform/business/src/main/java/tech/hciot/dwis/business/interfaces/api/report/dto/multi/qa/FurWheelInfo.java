package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

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
public class FurWheelInfo {

  private String heatRecordKey;
  private Integer tapSeq;
  private Integer ladleSeq;
  private String wheelSerial;
  private String heatCode;
  private String testCode;
  private String reworkCode;
  private String scrapCode;
  private String isConfirmScrap;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date scrapDate;
  private String isFinished;
  private String checkCode;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date stockDate;
  private String shippedNo;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date shippedDate;
  private String customerName;
}
