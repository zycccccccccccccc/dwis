package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.business.domain.model.Certificate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateInfo {

  private String contractNo;
  private String contractName;
  private String design;
  private Integer wheelW;
  private String shippedDate;
  private String shippedNo;
  private BigDecimal tapeSize;
  private String customerName;
  private String trainNo;
  private String balanceS;
  private String drawingNo;
  private String approbationNo;
  private String spec;
  private String transferRecordNo;
  private String steelClass;
  private String checkCode;
  private String batchNo;

  private List<Certificate> CertificateList;
}
