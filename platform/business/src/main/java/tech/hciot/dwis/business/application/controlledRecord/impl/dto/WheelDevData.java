package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class WheelDevData {

  private Date opeDT;
  private String shiftNo;
  private String wheelSerial;
  private String distance;
  private Integer rimDev;
  private Integer hubBack;
  private Integer hubFront;
  private Integer sideBack;
  private Integer sideFront;
  private Integer frontRim;
  private Integer backRim;
  private Integer diffRim;
  private String nickname;
  private String memo;
}
