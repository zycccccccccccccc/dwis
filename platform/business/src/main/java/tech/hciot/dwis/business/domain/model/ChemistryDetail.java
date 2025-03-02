package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class ChemistryDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer heatRecordId;
  private Integer ladleId;
  private Integer labId;
  private String furnaceSeq;
  private String sampleNo;
  @Builder.Default
  private BigDecimal c = new BigDecimal(0);
  @Builder.Default
  private BigDecimal si = new BigDecimal(0);
  @Builder.Default
  private BigDecimal mn = new BigDecimal(0);
  @Builder.Default
  private BigDecimal p = new BigDecimal(0);
  @Builder.Default
  private BigDecimal s = new BigDecimal(0);
  @Builder.Default
  private BigDecimal cr = new BigDecimal(0);
  @Builder.Default
  private BigDecimal ni = new BigDecimal(0);
  @Builder.Default
  private BigDecimal w = new BigDecimal(0);
  @Builder.Default
  private BigDecimal v = new BigDecimal(0);
  @Builder.Default
  private BigDecimal mo = new BigDecimal(0);
  @Builder.Default
  private BigDecimal ti = new BigDecimal(0);
  @Builder.Default
  private BigDecimal cu = new BigDecimal(0);
  @Builder.Default
  private BigDecimal al = new BigDecimal(0);
  @Builder.Default
  private BigDecimal b = new BigDecimal(0);
  @Builder.Default
  private BigDecimal co = new BigDecimal(0);
  @Builder.Default
  private BigDecimal sn = new BigDecimal(0);
  @Builder.Default
  private BigDecimal pb = new BigDecimal(0);
  @Builder.Default
  @Column(name = "[as]")
  private BigDecimal as = new BigDecimal(0);
  @Builder.Default
  private BigDecimal sb = new BigDecimal(0);
  @Builder.Default
  private BigDecimal bi = new BigDecimal(0);
  @Builder.Default
  private BigDecimal nb = new BigDecimal(0);
  @Builder.Default
  private BigDecimal ca = new BigDecimal(0);
  @Builder.Default
  private BigDecimal mg = new BigDecimal(0);
  @Builder.Default
  private BigDecimal ce = new BigDecimal(0);
  @Builder.Default
  private BigDecimal n = new BigDecimal(0);
  @Builder.Default
  private BigDecimal zr = new BigDecimal(0);
  @Builder.Default
  private BigDecimal bs = new BigDecimal(0);
  @Builder.Default
  private BigDecimal ns = new BigDecimal(0);
  @Builder.Default
  private BigDecimal nt = new BigDecimal(0);
  @Builder.Default
  private BigDecimal fe = new BigDecimal(0);
  private String opreId;

  @Builder.Default
  private Integer seqNoRepeat = 0;

  @Transient
  @Builder.Default
  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal aar1 = new BigDecimal(0);

  @Transient
  @Builder.Default
  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal tb1 = new BigDecimal(0);

  @Transient
  @Builder.Default
  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal tb2 = new BigDecimal(0);

  @Transient
  @Builder.Default
  @JsonSerialize(using = CustomerDoubleSerialize.class)
  private BigDecimal aar2 = new BigDecimal(0);

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createDate;

  @Transient
  private String labDate;

  @Transient
  private String labTime;

  public void computeOtherChemistry() {
    aar1 = BigDecimal.valueOf(doubleValue(cr)
                            + doubleValue(ni)
                            + doubleValue(mo)
                            + doubleValue(cu));

    tb1 = BigDecimal.valueOf(doubleValue(cr)
                           + doubleValue(ni)
                           + doubleValue(mo));

    tb2 = BigDecimal.valueOf(doubleValue(cr)
                           + doubleValue(ni)
                           + doubleValue(mo)
                           + doubleValue(cu)
                           + doubleValue(sn)
                           + doubleValue(v)
                           + doubleValue(ti)
                           + doubleValue(nb));

    aar2 =
        BigDecimal.valueOf(930
          - (570 * doubleValue(c)
            + 80 * doubleValue(mn)
            + 20 * doubleValue(si)
            + 50 * doubleValue(cr)
            + 30 * doubleValue(ni)
            + 20 * doubleValue(mo)
            + 20 * doubleValue(v)));
  }

  private double doubleValue(BigDecimal value) {
    return value == null ? 0 : value.doubleValue();
  }
}
