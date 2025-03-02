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
public class UsaSerialResponse {

  private String design;
  private String orderNo;
  private String spindleNo;
  private String netWeight;
  private String grossWeight;
  @JsonFormat(pattern = "MMM,yyyy", timezone = "GMT+8", locale = "en")
  private Date date;
}
