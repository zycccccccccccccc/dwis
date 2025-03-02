package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {

  @NotEmpty
  private String oldPassword;
  @NotEmpty
  private String newPassword;
}
