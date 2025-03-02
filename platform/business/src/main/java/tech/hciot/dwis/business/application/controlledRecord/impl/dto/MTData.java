package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class MTData {

  private Date opeDT;
  private String shiftNo;
  private Integer solutionAmount;
  private Integer magnAmount;
  private Integer dispAmount;
  private Integer defoAmount;
  private BigDecimal solutionDensity;
  private String solutionPre;
  private BigDecimal magnCurrent;
  private Integer lightCopeLeft;
  private Integer lightCopeRight;
  private Integer lightTread;
  private Integer lightDragLeft;
  private Integer lightDragRight;
  private BigDecimal whiteLight;
  private Integer striprevUp;
  private Integer striprevDown;
  private Integer remanenceIntensity;
  private String manufacturer;
  private String granularity;
  private String batchnoMT;
  private String roomTemp;
  private String operator;
  private String inspectorId;
}
