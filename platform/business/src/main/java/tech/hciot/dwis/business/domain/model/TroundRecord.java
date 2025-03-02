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
public class TroundRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotEmpty
  private String wheelSerial;
  @NotNull
  private Integer pro;
  @NotNull
  private BigDecimal brinnel1;
  @NotNull
  private BigDecimal brinnel2;
  @NotNull
  private BigDecimal brinnel3;
  @NotNull
  private BigDecimal brinnel4;
  private BigDecimal roundDiffer;
  private Integer ts;
  @NotEmpty
  private String operator;
  @NotEmpty
  protected String inspectorId;
  @NotEmpty
  private String shiftNo;

  @Builder.Default
  public Integer isInspecCheck = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
