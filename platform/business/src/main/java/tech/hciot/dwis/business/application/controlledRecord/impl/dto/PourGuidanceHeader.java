package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PourGuidanceHeader {

  private Date castDate;
  private Integer furnaceNo;
  private Integer heatSeq;
  private Integer tapSeq;
  private BigDecimal outSteelTemp;
  private BigDecimal bottomTemp;
  private Integer plantTemp;
  private String pourId;
  private String furnaceId;
  private String modelId;
  private String modiId;
  private String bagNo;
  private String ladleNo;
  private String coreSetterId;
  private String pourLeaderId;
  private String pourDirectId;
  private String openId;
  private String craneInId;
  private Date l1;
  private Date l2;
  private Date l3;
  private Date l4;
  private BigDecimal cycletimeConveyor;
}
