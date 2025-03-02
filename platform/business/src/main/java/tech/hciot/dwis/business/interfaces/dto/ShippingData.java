package tech.hciot.dwis.business.interfaces.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ShippingData {

  private String checkCode;
  private String wheelSerial;
  private String design;
  private String shippedNo;
  private Date shippedDate;
  private String shelfNumber;
}
