package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MecProperty {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String wheelSerial;
  private String inspectorId;
  private String design;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date testDate;
  @Column(name = "[test_no]")
  private String mecSerial;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date reportDate;
  @Default
  private Integer retest = 0;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date pourBatch1;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date pourBatch2;
  private BigDecimal residualStress;
  private Integer tensile;
  private BigDecimal elongation;
  private Integer hardnessLocation1;
  private Integer hardnessLocation2;
  private Integer hardnessLocation3;
  private BigDecimal impactAvg;
  private BigDecimal impactMin;
  private BigDecimal impactLocation1;
  private BigDecimal impactLocation2;
  private BigDecimal impactLocation3;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Column(name = "[ope_d_t]")
  private Date opeDT;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createDate;
}
