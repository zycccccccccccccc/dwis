package tech.hciot.dwis.business.interfaces.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {

  private String id;
  private String roleName;
  @JsonInclude(Include.NON_NULL)
  private String remark;
  @JsonInclude(Include.NON_NULL)
  private List<String> authorities;
}
