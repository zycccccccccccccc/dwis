package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class TapeData {

  private Date opeDT;
  private String shiftNo;
  private String wheelSerial;
  private String design;
  private BigDecimal tapeSize;
  private Integer ts;
  private String nickname;
  private String memo;
}
