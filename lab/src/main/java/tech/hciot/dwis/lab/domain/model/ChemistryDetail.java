package tech.hciot.dwis.lab.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.lab.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemistryDetail {

  private Integer heatRecordId;
  private Integer ladleId;
  private Integer labId;
  private String furnaceSeq;
  private String sampleNo;

  @Order(1)
  private BigDecimal c;
  @Order(2)
  private BigDecimal si;
  @Order(3)
  private BigDecimal mn;
  @Order(4)
  private BigDecimal p;
  @Order(5)
  private BigDecimal s;
  @Order(6)
  private BigDecimal cr;
  @Order(7)
  private BigDecimal ni;
  @Order(8)
  private BigDecimal mo;
  @Order(9)
  private BigDecimal cu;
  @Order(10)
  private BigDecimal al;
  @Order(11)
  private BigDecimal ti;
  @Order(12)
  private BigDecimal v;
  @Order(13)
  private BigDecimal nb;
  @Order(14)
  private BigDecimal b;
  @Order(15)
  private BigDecimal sn;
  @Order(16)
  private BigDecimal pb;

  private String opreId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createDate;
}
