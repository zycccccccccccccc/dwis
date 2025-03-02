package tech.hciot.dwis.business.interfaces.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckMecserialRequest {
  @NotEmpty
  private String acceptanceNo;
}
