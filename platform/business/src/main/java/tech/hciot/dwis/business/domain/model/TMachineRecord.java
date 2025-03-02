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
import org.hibernate.annotations.DynamicInsert;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicInsert
@EqualsAndHashCode(callSuper = true)
public class TMachineRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotNull
  private Integer machineNo;
  private String operator;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;
  @NotNull
  private BigDecimal tS1;
  @NotNull
  private Integer tS2;
  private BigDecimal rimWidth;
  private BigDecimal hubLength;
  private Integer flangeTreadProfile;
  private BigDecimal rollingCircleDia;
  private Integer tChamfer;
  private BigDecimal rimdev1;
  private BigDecimal rimdev2;
  private BigDecimal rimdev3;
  private String reworkCode;
  private Integer isRollingDiaCheck;
  private Integer isCheck;
  private Integer isInspecCheck;
  private Integer isMeasureCheck;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private String memo;
}
