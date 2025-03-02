package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class ShotTestData {

  private Date opeDT;
  private String shiftNo;
  private Integer peeningTime;
  private BigDecimal intensityFront;
  private BigDecimal intensityBack;
  private Integer coverageFront;
  private Integer coverageBack;
  private Integer sieveNo;
  private String shotType;
  private Integer amountOnsieve;
  private String ShotpeenerNo;
  private String nickname;
  private String memo;
}
