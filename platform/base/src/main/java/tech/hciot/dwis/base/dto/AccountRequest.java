package tech.hciot.dwis.base.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

  @NotEmpty
  private String username;
  @NotEmpty
  private String password;
  @NotEmpty
  private String role;
  private String nickname;
  private String mobile;
}
