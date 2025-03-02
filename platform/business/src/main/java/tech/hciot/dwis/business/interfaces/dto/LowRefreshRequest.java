package tech.hciot.dwis.business.interfaces.dto;

import lombok.Data;

@Data
public class LowRefreshRequest {

  private Integer lowHeatInShift;
  private String lowHeatInId;
  private String lowHeatInOperator;
  private Integer lowHeatOutShift;
  private String lowHeatOutId;
  private String lowHeatOutOperator;
}
