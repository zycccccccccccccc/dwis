package tech.hciot.dwis.business.interfaces.dto;

import lombok.Data;

@Data
public class ChangeTrainRequest {

  private String shippedDate;
  private String trainNo;
  private String customerId;
}
