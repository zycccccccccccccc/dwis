package tech.hciot.dwis.business.interfaces.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class ModifyStaffRequest {

  private String operatorName;
  private String operatorId;
  private String teamLeaderId;
  private Integer depId;
  private Integer stationId;
  private Integer enabled;
  private String memo;
  private String mobile;
  private String email;
  private JSONArray location;
  private Integer isLeader;
}
