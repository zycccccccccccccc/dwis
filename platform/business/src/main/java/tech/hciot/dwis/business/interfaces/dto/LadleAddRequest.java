package tech.hciot.dwis.business.interfaces.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class LadleAddRequest {

  @NotNull
  private Integer heatRecordId;
  @NotNull
  private Integer ladleSeq;
  @NotNull
  private BigDecimal c;
  @NotNull
  private BigDecimal si;
  @NotNull
  private BigDecimal mn;
  @NotNull
  private String operatorId;
  @NotNull
  private String type;

  private BigDecimal al;
}
