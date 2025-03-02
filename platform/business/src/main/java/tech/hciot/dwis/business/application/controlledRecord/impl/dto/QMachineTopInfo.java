package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class QMachineTopInfo {

  private String operator;
  private Integer machineNo;
  private Date opeDT;
  private String inspectorId;
  private Integer isCheck;
  private BigDecimal claw1;
  private BigDecimal claw1Sym;
  private BigDecimal claw2;
  private BigDecimal claw3;
  private BigDecimal rest1;
  private BigDecimal rest2;
  private BigDecimal rest3;
}
