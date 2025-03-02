package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipWheelRecord {

  private String design;
  private String customer;
  private String shippedId;
  private String shippedDate;
  private String shippedNo;
  private String trainNo;
  private String balanceS;
  private Integer amount;
  private List<String> wheelList;
}
