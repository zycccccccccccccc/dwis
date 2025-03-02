package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XNWheelResponse {

  private Integer ladleId;
  private String ladleKey;
  private String C;
  private String Si;
  private String Mn;
  private Integer quantity;
  private Integer isCheckPass;
}
