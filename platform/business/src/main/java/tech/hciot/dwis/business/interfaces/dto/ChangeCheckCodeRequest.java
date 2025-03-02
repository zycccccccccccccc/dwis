package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangeCheckCodeRequest {

  @NotEmpty
  @Length(min = 8)
  private String checkCode;
}
