package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddRoleRequest {

  @NotEmpty
  private String roleName;
  private String remark;
  private List<String> authorities;
}
