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
public class KMachineRecord extends InspecCheckable {

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
  @NotEmpty
  private String location;
  @NotNull
  private Integer kS1;
  @NotNull
  private Integer kS2;
  private BigDecimal concentricity;
  private BigDecimal boreDia;
  private String reworkCode;
  private Integer isCheck;
  private Integer isInspecCheck;
  private Integer isMeasureCheck;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
  private String memo;
}
