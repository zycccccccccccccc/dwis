package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class ModifyLabRequest {

  private Integer heatRecordId;
  private Integer ladleId;
  private Integer labId;
  private String furnaceSeq;
  private String sampleNo;
  private BigDecimal c;
  private BigDecimal si;
  private BigDecimal mn;
  private BigDecimal p;
  private BigDecimal s;
  private BigDecimal cr;
  private BigDecimal ni;
  private BigDecimal w;
  private BigDecimal v;
  private BigDecimal mo;
  private BigDecimal ti;
  private BigDecimal cu;
  private BigDecimal al;
  private BigDecimal b;
  private BigDecimal co;
  private BigDecimal sn;
  private BigDecimal pb;
  private BigDecimal as;
  private BigDecimal sb;
  private BigDecimal bi;
  private BigDecimal nb;
  private BigDecimal ca;
  private BigDecimal mg;
  private BigDecimal ce;
  private BigDecimal n;
  private BigDecimal zr;
  private BigDecimal bs;
  private BigDecimal ns;
  private BigDecimal nt;
  private BigDecimal fe;
  private String operId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
}
