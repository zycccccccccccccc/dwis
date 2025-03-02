package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportRecordResponse {

  private String wheelSerial;
  private String design;
  private String boreSize;
  private BigDecimal tapeSize;
  private Integer wheelW;
  private Integer brinnelReading;
  private Integer balanceV;
  private Integer balanceA;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;
}
