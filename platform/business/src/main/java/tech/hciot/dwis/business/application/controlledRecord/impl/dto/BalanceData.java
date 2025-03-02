package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class BalanceData {

  private Date opeDT;
  private String shiftNo;
  private String wheelSerial;
  private String design;
  private Integer ts;
  private BigDecimal balanceV180;
  private Integer balanceA180;
  private BigDecimal balanceV270;
  private Integer balanceA270;
  private String operator;
  private String inspectorId;
  private Integer isInspecCheck;
}
