package tech.hciot.dwis.base.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

  private String username;
  private String nickname;
  private String mobile;
  private String email;
  private String avatar;
  private Integer status;
  private List<String> roles;
  private Integer loginStatus;
}
