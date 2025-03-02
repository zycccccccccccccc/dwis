package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class ShotData {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastPre;
  private String wheelSerial;
  private String shippedNo;
  private String scrapCode;
  private Integer finished;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastBalance;
}
