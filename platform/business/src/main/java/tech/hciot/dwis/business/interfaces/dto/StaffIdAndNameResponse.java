package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffIdAndNameResponse {
  private String operatorId;
  private String operatorName;
}
