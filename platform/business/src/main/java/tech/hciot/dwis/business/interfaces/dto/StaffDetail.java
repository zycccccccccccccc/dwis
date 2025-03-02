package tech.hciot.dwis.business.interfaces.dto;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffDetail {

  private String id;
  private String operatorName;
  private String operatorId;
  private String teamLeaderId;
  private Integer depId;
  private String depName;
  private Integer stationId;
  private String stationName;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
  private String memo;
  private Integer status;
  private String mobile;
  private String email;
  private JSONArray location;
  private Integer enabled;
  private Integer isLeader;
}
