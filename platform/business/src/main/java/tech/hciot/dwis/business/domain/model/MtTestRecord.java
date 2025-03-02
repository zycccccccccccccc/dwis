package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class MtTestRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String eqModel;
  private String eqNo;
  private String roomTemp;
  private String solutionPre;
  private Integer solutionAmount;
  private Integer magnAmount;
  private Integer dispAmount;
  private Integer defoAmount;
  private BigDecimal solutionDensity;
  private BigDecimal magnCurrent;
  private Integer lightCopeLeft;
  private Integer lightCopeRight;
  private Integer lightTread;
  private Integer lightDragLeft;
  private Integer lightDragRight;
  private BigDecimal whiteLight;
  private Integer striprevUp;
  private Integer striprevDown;
  private Integer remanenceIntensity;
  private String batchnoMt;
  private Integer ts;
  private String operator;
  private String inspectorId;
  private String shiftNo;

  @Builder.Default
  private Integer isInspecCheck = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
