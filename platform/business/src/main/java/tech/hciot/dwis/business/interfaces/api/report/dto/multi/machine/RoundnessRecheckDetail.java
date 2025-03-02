package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoundnessRecheckDetail {

  private Integer sn;
  private String operator;
  private String wheelSerial;
  private BigDecimal tapeSize;
}
