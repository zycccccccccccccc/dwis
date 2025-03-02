package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ThreeHbData {

  private Date testDate;
  private String shiftNo;
  private String wheelSerial;
  private Integer brinnel1;
  private Integer brinnel2;
  private Integer brinnel3;
  private Integer difference;
  private String result;
  private Integer reTest;
  private Integer isInspecCheck;
  private String operator;
}
