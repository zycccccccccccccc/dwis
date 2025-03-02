package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

  private String username;
  private String nickname;
  private String mobile;
  private String email;
  private Integer status;
  private List<String> roles;
  private Integer loginStatus;
  private String location;
  private Integer depId;
  private String depName;
}
