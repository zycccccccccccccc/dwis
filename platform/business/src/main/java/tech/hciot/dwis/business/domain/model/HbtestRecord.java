package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
public class HbtestRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @NotNull
  private Date testDate;
  @NotNull
  private Integer hbNo;
  @NotEmpty
  private String testBlockNo;
  @NotNull
  private Integer standValue;
  @NotNull
  private Integer testResult;
  @NotNull
  private BigDecimal indentaDia;
  @NotNull
  private BigDecimal mIndentaDia;
  private BigDecimal devIndentaDia;
  private Integer ts;
  @NotEmpty
  private String operator;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String shiftNo;

  @Builder.Default
  private Integer isInspecCheck = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
