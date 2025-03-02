package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class UTData {

  private Date opeDT;
  private String shiftNo;
  private String wheelSerial;
  private String design;
  private Integer tDb;
  private Integer j1;
  private Integer j2;
  private Integer j3;
  private Integer j4;
  private Integer j5;
  private Integer j6;
  private Integer j7;
  private Integer bDb;
  private Integer z1;
  private Integer z2;
  private Integer z3;
  private Integer z4;
  private Integer z5;
  private Integer wheelCheck;
  private Integer probeCheck;
  private String roomTemp;
  private String operator;
  private String inspectorId;
}
