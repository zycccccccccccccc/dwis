package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class FurnaceTapInfo {

  private Date castDate;
  private String tapNo;
  private String chargeTankNo;
  private Date firstPoweronTime;
  private Date tapTime;
  private Date useTime;
  private Integer mtotalWeight;
  private BigDecimal emeterReading;
  private BigDecimal emeterReadingStart;
  private BigDecimal thistimeEconsumption;
  private String delayedCode;
  private Integer o2Flow;
  private Integer o2Start;
  private Integer thistimeO2UseQuantity;
  private String fbottomContion;
  private String fwallContion;
  private String froofContion;
  private String tappingSpoutContion;
  private Integer fbottomUsage;
  private Integer fwallUsage;
  private Integer froofUsage;
  private Integer tappingSpoutUsage;
  private String patchingPosition;
  private String rammingPosition;
  private Integer patchingAmount;
  private Integer rammingAmount;
  private Integer electrodeUseQuantity;
  private Integer electrodeBrokenQuantity;
  private Integer plugUseQuantity;
  private Integer plugBrokenQuantity;
  private String memo;
  private String leader;
  private String furnaceLeader;
  private String materialStaff;
  private Date tapDuration;
  private Integer tapTemp;
}
