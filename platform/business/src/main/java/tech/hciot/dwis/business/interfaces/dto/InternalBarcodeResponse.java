package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalBarcodeResponse {

  @Default
  private String manufacturer = "DCACC";
  private String wheelSerial;
  private String design;
  @JsonFormat(pattern = "yyyy/M/d h:mm:ss", timezone = "GMT+8")
  private Date lastBarcode;
  private Integer wheelW;
  private Integer boreSize;
  private BigDecimal tapeSize;
  private String balanceS;
  private String dataMatrix;
}
