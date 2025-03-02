package tech.hciot.dwis.business.interfaces.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestWheelResponse {

  private String wheelSerial;
  private String design;
}
