package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class HeatHeader {

  private Date hiHeatInDate;
  private Integer heatLine;
  private String cutId;
  private Integer hiHeatInShift;
  private String hiHeatInId;
  private String hiHeatInOperator;
  private Integer hiHeatOutShift;
  private String hiHeatOutId;
  private String hiHeatOutOperator;
  private Integer lowHeatInShift;
  private String lowHeatInId;
  private String lowHeatInOperator;
  private Integer lowHeatOutShift;
  private String lowHeatOutId;
  private String lowHeatOutOperator;
  private String targetTemp;
  private Date actualCycle;
  private Date timeChecked;
  private Date treadQuenchDelay;
  private Date treadTimeChecked;
  private Date sprayTime;
  private Date sprayTimeChecked;
  private BigDecimal waterPressure;
  private Date waterPressureTimeChecked;
  private Integer waterTemp;
  private Date waterTempTimeChecked;
  private String targetTempLow;
  private Date actualCycleLow;
  private Date timeCheckedLow;
}
