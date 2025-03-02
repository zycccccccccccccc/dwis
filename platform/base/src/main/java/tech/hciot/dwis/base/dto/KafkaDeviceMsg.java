package tech.hciot.dwis.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaDeviceMsg<T> {

  private String productKey;
  private String deviceName;
  private String coralDeviceId;
  private String tenantId;
  private T data;
}
