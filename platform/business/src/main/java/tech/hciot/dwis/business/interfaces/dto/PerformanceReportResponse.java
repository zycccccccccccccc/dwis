package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PerformanceReportResponse {

  private String mecSerial;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date reportDate;
  private String wheelSerial;
  private String design;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date testDate;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date pourBatch1;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date pourBatch2;
  private String inspectorId;
  private BigDecimal c;
  private BigDecimal si;
  private BigDecimal mn;
  private BigDecimal p;
  private BigDecimal s;
  private BigDecimal cr;
  private BigDecimal cu;
  private BigDecimal mo;
  private BigDecimal ni;
  private BigDecimal v;
  private BigDecimal al;
  private BigDecimal sn;
  private BigDecimal ti;
  private BigDecimal nb;
  private Integer tensile;
  private BigDecimal elongation;
  private BigDecimal impactAvg;
  private BigDecimal impactLocation1;
  private BigDecimal impactLocation2;
  private BigDecimal impactLocation3;
  private BigDecimal impactMin;
  private Integer hardnessLocation1;
  private Integer hardnessLocation2;
  private Integer hardnessLocation3;
  private BigDecimal residualStress;

}
