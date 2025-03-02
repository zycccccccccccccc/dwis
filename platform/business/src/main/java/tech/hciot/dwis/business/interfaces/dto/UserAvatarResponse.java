package tech.hciot.dwis.business.interfaces.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAvatarResponse {

  private String avatar;
}
