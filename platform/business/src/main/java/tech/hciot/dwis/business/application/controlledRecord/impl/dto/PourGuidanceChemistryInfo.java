package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class PourGuidanceChemistryInfo {

  private String sampleNo;
  private BigDecimal c;
  private BigDecimal si;
  private BigDecimal mn;
  private BigDecimal p;
  private BigDecimal s;
  private BigDecimal al;
  private BigDecimal cr;
  private BigDecimal cu;
  private BigDecimal sn;
  private BigDecimal tb1;
  private BigDecimal tb2;
  private BigDecimal aar1;
  private BigDecimal aar2;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createDate;
}
