package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LadleAddsResponse {
  private Integer heatRecordId;
  private Map<String, Map<String, BigDecimal>> ladleAdds;
}
