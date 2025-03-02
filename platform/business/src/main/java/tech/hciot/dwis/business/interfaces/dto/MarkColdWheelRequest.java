package tech.hciot.dwis.business.interfaces.dto;

import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkColdWheelRequest {

  @NotEmpty
  private String inspectorId;
  @NotNull
  private Date castDate;
  @NotNull
  private Integer tapSeq;
  @NotNull
  private Integer pitNo;
  @NotNull
  private Integer pitSeq;
}
