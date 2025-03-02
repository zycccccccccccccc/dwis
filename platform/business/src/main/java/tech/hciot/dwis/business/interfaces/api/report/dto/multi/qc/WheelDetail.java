package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class WheelDetail {

  private String heatRecordKey;
  private Integer tapSeq;
  private Integer ladleSeq;
  private String wheelSerial;
  private String openTimeAct;
  private String scrapCode;
  private Integer confirmedScrap;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date scrapDate;
  private Integer pre;
  private Integer finalTimes;
  private Integer ultra;
  private Integer balance;
  private Integer finished;
  private String mecSerial;
}
