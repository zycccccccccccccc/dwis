package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PourGuidanceData {

  private Integer pourId;
  private String wheelSerial;
  private String dragNo;
  private String copeNo;
  private Integer bagNo;
  private Date pourTime;
  private BigDecimal ladleTemp;
  private Date openTimeCal;
  private Date openTimeAct;
  private Integer pitNo;
  private Integer pitCount;
  private Date inPitDateTime;
  private String scrapCode;
  private String design;

}
