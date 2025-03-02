package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckMecserialResponse {

  private String mecSerial;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date reportDate;
  private Integer quantity;
}
