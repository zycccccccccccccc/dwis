package tech.hciot.dwis.business.domain.model;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "v_certificate")
public class Certificate {

  @Id
  private Integer id;
  private String ladleRecordKey;
  private String wheelSerial;
  private String shelfNumber;
  private String design;
  private Integer wheelW;
  private String shippedDate;
  private String shippedNo;
  private BigDecimal tapeSize;
  private String customerName;
  private String trainNo;
  private String balanceS;
  private Integer brinnelReading;
  private String drawingNo;
  private String approbationNo;
  private String spec;
  private String transferRecordNo;
  private String steelClass;
  private BigDecimal c;
  private BigDecimal mn;
  private BigDecimal p;
  private BigDecimal s;
  private BigDecimal si;
  private BigDecimal cr;
  private BigDecimal ni;
  private BigDecimal mo;
  private BigDecimal cu;
  private BigDecimal nb;
  private BigDecimal v;
  private BigDecimal ti;
  private BigDecimal al;
  private BigDecimal h;
  private String checkCode;
  private String batchNo;

  @Transient
  private String no;

  @Transient
  private String grindDepth;
}
