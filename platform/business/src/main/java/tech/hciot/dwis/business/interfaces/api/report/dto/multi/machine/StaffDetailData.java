package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class StaffDetailData {

  private String machineNo;
  private String wheelSerial;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;
  private String reworkHoldCode;
  private Integer isCheck;
  private Integer isInspecCheck;
  private Integer isMeasureCheck;
  private BigDecimal s1;
  private Integer s2;
  private BigDecimal data1;
  private BigDecimal data2;
  private String data3;
}
