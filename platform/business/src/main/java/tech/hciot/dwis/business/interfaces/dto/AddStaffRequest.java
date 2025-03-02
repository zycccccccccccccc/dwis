package tech.hciot.dwis.business.interfaces.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class AddStaffRequest {

  private String operatorName;
  private String operatorId;
  private String teamLeaderId;
  private Integer depId;
  private Integer stationId;
  private String memo;
  private String password;
  private String mobile;
  private String email;
  private String roleId;
  private JSONArray location;
  private Integer enabled;
  private Integer isLeader;
}
