package tech.hciot.dwis.business.interfaces.dto;

import static tech.hciot.dwis.base.util.CommonUtil.getDoubleValue;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.business.domain.model.CustomerDoubleSerialize;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChemistryDetailResponse {

  private Integer ladleId;
  private String ladleRecordKey;
  private String wheelSerial;
  private String design;
  private String scrapCode;
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

  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal aar1;

  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal tb1;

  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal tb2;

  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal tb3;

  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal aar2;

  private List<String> highlight;

  private List<String> yellowLight;

  public void computeOtherChemistry() {
    aar1 = BigDecimal.valueOf(getDoubleValue(cr)
        + getDoubleValue(ni)
        + getDoubleValue(mo)
        + getDoubleValue(cu));

    tb1 = BigDecimal.valueOf(getDoubleValue(cr)
        + getDoubleValue(ni)
        + getDoubleValue(mo));

    tb2 = BigDecimal.valueOf(getDoubleValue(cr)
        + getDoubleValue(ni)
        + getDoubleValue(mo)
        + getDoubleValue(cu)
        + getDoubleValue(sn)
        + getDoubleValue(v)
        + getDoubleValue(ti)
        + getDoubleValue(nb));

    tb3 = BigDecimal.valueOf(getDoubleValue(cr)
        + getDoubleValue(mo)
        + getDoubleValue(ni)
        + getDoubleValue(cu)
        + getDoubleValue(v));

    aar2 =
        BigDecimal.valueOf(930
            - (570 * getDoubleValue(c)
            + 80 * getDoubleValue(mn)
            + 20 * getDoubleValue(si)
            + 50 * getDoubleValue(cr)
            + 30 * getDoubleValue(ni)
            + 20 * getDoubleValue(mo)
            + 20 * getDoubleValue(v)));
  }
}
