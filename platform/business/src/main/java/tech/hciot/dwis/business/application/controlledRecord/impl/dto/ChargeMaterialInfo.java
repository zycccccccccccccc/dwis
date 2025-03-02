package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class ChargeMaterialInfo {

  private Integer times;
  private Date chargeTime;
  private Date poweronTime;
  private BigDecimal purchaseWheelWeight;
  private BigDecimal wheelReturnsWeight;
  private BigDecimal railWeight;
  private BigDecimal bobsAndHeadsWeight;
  private BigDecimal roughReturnsWeight;
  private BigDecimal wheelTyreWeight;
  private BigDecimal turnningWeight;
  private BigDecimal hammerWeight;
  private BigDecimal guardRailBuckleWeight;
  private BigDecimal steelBoardWeight;
  private BigDecimal couplerWeight;
  private BigDecimal hboardWeight;
  private BigDecimal mtotalWeight;
}
